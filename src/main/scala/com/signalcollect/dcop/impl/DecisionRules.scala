package com.signalcollect.dcop.impl

import scala.util.Random
import com.signalcollect.dcop.modules._

//TODO: Once we want a different type of utility, to plug-in a Utility type param for the traits and for the expectedUtilities vals. It must be compatible with the max function.
//trait NashEquilibriumConvergence[AgentId, Action, Config <: Configuration[AgentId, Action]] extends DecisionRule[AgentId, Action, Config, Double] with TargetFunction[AgentId, Action, Config, Double] {
//
//  override protected def isConvergedGivenUtilitiesAndMaxUtility(
//    c: Config,
//    expectedUtilities: Map[Action, Double],
//    maxUtility: Double): Boolean = {
//    val currentUtility = expectedUtilities(c.centralVariableValue)
//    maxUtility == currentUtility
//  }
//
//  override def isConverged(c: Config): Boolean = {
//    val expectedUtilities: Map[Action, Double] = computeExpectedUtilities(c)
//    val maxUtility = expectedUtilities.values.max
//    isConvergedGivenUtilitiesAndMaxUtility(c, expectedUtilities, maxUtility)
//  }
//}
//
///**
// * Is converged only when there are no more conflicts. Not based on the target or utility function.
// */
//trait ZeroConflictConvergence[AgentId, Action, Config <: Configuration[AgentId, Action]] extends DecisionRule[AgentId, Action, Config, Double] {
//
//  /**
//   * No delegation between isConverged and isConvergedGivenUtilitiesAndMaxUtility
//   */
//  override def isConverged(c: Config): Boolean = {
//    c.computeExpectedNumberOfConflicts == 0
//  }
//}
//
///**
// * Main use for hard constraints, where you have negative utility
// * for violating constraints and reach 0 utility only when no constraints are violated.
// * One example is: ConflictBasedVertexColoringUtility
// */
//trait ZeroUtilityConvergence[AgentId, Action, Config <: Configuration[AgentId, Action]] extends DecisionRule[AgentId, Action, Config, Double] with TargetFunction[AgentId, Action, Config, Double] {
//
//  override protected def isConvergedGivenUtilitiesAndMaxUtility(
//    c: Config,
//    expectedUtilities: Map[Action, Double],
//    maxUtility: Double): Boolean = {
//    val currentUtility = expectedUtilities(c.centralVariableValue)
//    currentUtility == 0
//  }
//
//  /**
//   * No delegation between isConverged and isConvergedGivenUtilitiesAndMaxUtility
//   */
//  override def isConverged(c: Config): Boolean = {
//    val expectedUtilities: Map[Action, Double] = computeExpectedUtilities(c)
//    val currentUtility = expectedUtilities(c.centralVariableValue)
//    currentUtility == 0
//  }
//}

trait ArgmaxADecisionRule[AgentId, Action, Config <: Configuration[AgentId, Action]] extends DecisionRule[AgentId, Action, Config, Double] with TargetFunction[AgentId, Action, Config, Double] {

  def computeMove(c: Config) = {
    val expectedUtilities: Map[Action, Double] = computeExpectedUtilities(c)
    val maxUtility = expectedUtilities.values.max
    if (isConvergedGivenUtilitiesAndMaxUtility(c, expectedUtilities, maxUtility)) {
      c.centralVariableValue
    } else {
      val maxUtilityMoves: Seq[Action] = expectedUtilities.filter(_._2 == maxUtility).map(_._1).toSeq
      val chosenMaxUtilityMove = maxUtilityMoves(Random.nextInt(maxUtilityMoves.size))
      chosenMaxUtilityMove
    }
  }

}

trait ArgmaxBDecisionRule[AgentId, Action, Config <: Configuration[AgentId, Action]] extends ArgmaxADecisionRule[AgentId, Action, Config] {

  //TODO: Rewrite conditions for computeMove like in the Exploration version
  override def computeMove(c: Config) = {
    val expectedUtilities: Map[Action, Double] = computeExpectedUtilities(c)
    val maxUtility = expectedUtilities.values.max
    val maxUtilityMoves: Seq[Action] = expectedUtilities.filter(_._2 == maxUtility).map(_._1).toSeq
    val numberOfMaxUtilityMoves = maxUtilityMoves.size

    //If we are converged already don't stir the boat
    // Attention! If isConverged no longer depends on the utility so 
    // the maxUtility move may not be the current move anymore...
    if ((isConvergedGivenUtilitiesAndMaxUtility(c, expectedUtilities, maxUtility)) &&
      (maxUtilityMoves.contains(c.centralVariableValue)) &&
      (c.computeExpectedNumberOfConflicts == 0)) {
      c.centralVariableValue
    } else {
      val chosenMaxUtilityMove = maxUtilityMoves(Random.nextInt(maxUtilityMoves.size))
      chosenMaxUtilityMove
    }
  }
}

trait ExplorerArgmaxBDecisionRule[AgentId, Action, Config <: Configuration[AgentId, Action]] extends ArgmaxADecisionRule[AgentId, Action, Config] {

  def expl: Double

  override def computeMove(c: Config) = {
    val expectedUtilities: Map[Action, Double] = computeExpectedUtilities(c)
    val maxUtility = expectedUtilities.values.max
    val maxUtilityMoves: Seq[Action] = expectedUtilities.filter(_._2 == maxUtility).map(_._1).toSeq
    val numberOfMaxUtilityMoves = maxUtilityMoves.size

    def hasNoConflictsAtNashEquilibrium =
      (maxUtilityMoves.contains(c.centralVariableValue)) &&
        (c.computeExpectedNumberOfConflicts == 0)

    def hasConflictsAtNashEquilibrium =
      (maxUtilityMoves.contains(c.centralVariableValue)) &&
        (c.computeExpectedNumberOfConflicts > 0)

    if (hasNoConflictsAtNashEquilibrium) {
      c.centralVariableValue
    } else { // If there are still conflicts we might explore with very low probability
      if (hasConflictsAtNashEquilibrium && (Random.nextDouble < expl)) { //we added that maxDelta ==0
        val exploringMove = c.domain.toSeq(Random.nextInt(c.domain.size))
        exploringMove
      } else { // We randomly choose one of the solutions that give us the maximum
        val chosenMaxUtilityMove = maxUtilityMoves(Random.nextInt(maxUtilityMoves.size))
        chosenMaxUtilityMove
      }
    }
  }
}

trait SimulatedAnnealingDecisionRule[AgentId, Action, Config <: Configuration[AgentId, Action]] extends ArgmaxADecisionRule[AgentId, Action, Config] {

  def const: Double
  def k: Double
  var iteration = 0

  override def computeMove(c: Config) = {
    iteration += 1
    val randomMove = c.domain.toSeq(Random.nextInt(c.domain.size))
    val expectedUtilities = computeExpectedUtilities(c).toMap[Action, Double]
    val delta = expectedUtilities.getOrElse[Double](randomMove, -1) - expectedUtilities.getOrElse[Double](c.centralVariableValue, -1)
    if (delta <= 0 && Random.nextDouble <= scala.math.exp(delta / iteration)) {
      randomMove
    } else {
      c.centralVariableValue
    }
  }
}

trait LinearProbabilisticDecisionRule[AgentId, Action, Config <: Configuration[AgentId, Action]] extends ArgmaxADecisionRule[AgentId, Action, Config] {

  def eta: Double

  /*
   * In the case where we have a flat distribution and normFactor would be 0, the function should return the first action. 
   */
  override def computeMove(c: Config): Action = {
    val expectedUtilities: Map[Action, Double] = computeExpectedUtilities(c)
    val normFactor = expectedUtilities.values.sum
    val selectionProb = Random.nextDouble

    var partialSum: Double = 0.0
    for (action <- expectedUtilities.keys) {
      partialSum += expectedUtilities(action)
      if (selectionProb * normFactor <= partialSum) {
        return action
      }
    }
    throw new Exception("This code should be unreachable.")
  }

}
