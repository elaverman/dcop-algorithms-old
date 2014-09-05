package com.signalcollect.dcop.impl

import scala.util.Random
import com.signalcollect.dcop.modules._

trait DecisionRules[AgentId, Action] extends DecisionRuleModule[AgentId, Action] {
  this: ConfigurationModule[AgentId, Action] =>

  //    object RandomDecisionRule extends DecisionRule {
  //       def computeMove(c: Config) = {
  //         val randomMove: Action = c.domain.toSeq(Random.nextInt(c.domain.size))
  //         randomMove
  //       }
  //    }

}

trait DecisionRulesWithTargetFunctions[AgentId, Action] extends DecisionRuleModule[AgentId, Action] {
  this: TargetFunctionModule[AgentId, Action] with ConfigurationModule[AgentId, Action] =>

  trait NashEquilibriumConvergence extends DecisionRule {
    this: TargetFunction =>

    override protected def isConvergedGivenUtilitiesAndMaxUtility(
      c: Config,
      expectedUtilities: Map[Action, Utility],
      maxUtility: Utility): Boolean = {
      val currentUtility = expectedUtilities(c.centralVariableValue)
      maxUtility == currentUtility
    }

    override def isConverged(c: Config): Boolean = {
      val expectedUtilities: Map[Action, Utility] = computeExpectedUtilities(c)
      val maxUtility = expectedUtilities.values.max
      isConvergedGivenUtilitiesAndMaxUtility(c, expectedUtilities, maxUtility)
    }
  }

  /**
   * Is converged only when there are no more conflicts. Not based on the target or utility function.
   */
  trait ZeroConflictConvergence extends DecisionRule {

    /**
     * No delegation between isConverged and isConvergedGivenUtilitiesAndMaxUtility
     */
    override def isConverged(c: Config): Boolean = {
      c.computeExpectedNumberOfConflicts == 0
    }
  }

  /**
   * Main use for hard constraints, where you have negative utility
   * for violating constraints and reach 0 utility only when no constraints are violated.
   * One example is: ConflictBasedVertexColoringUtility
   */
  trait ZeroUtilityConvergence extends DecisionRule {
    this: TargetFunction =>

    override protected def isConvergedGivenUtilitiesAndMaxUtility(
      c: Config,
      expectedUtilities: Map[Action, Utility],
      maxUtility: Utility): Boolean = {
      val currentUtility = expectedUtilities(c.centralVariableValue)
      currentUtility == 0
    }

    /**
     * No delegation between isConverged and isConvergedGivenUtilitiesAndMaxUtility
     */
    override def isConverged(c: Config): Boolean = {
      val expectedUtilities: Map[Action, Utility] = computeExpectedUtilities(c)
      val currentUtility = expectedUtilities(c.centralVariableValue)
      currentUtility == 0
    }
  }

  trait ArgmaxADecisionRule extends DecisionRule {
    this: TargetFunction =>

    def computeMove(c: Config) = {
      val expectedUtilities: Map[Action, Utility] = computeExpectedUtilities(c)
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

  trait ArgmaxBDecisionRule extends ArgmaxADecisionRule {
    this: TargetFunction =>
    //TODO: Rewrite conditions for computeMove like in the Exploration version
    override def computeMove(c: Config) = {
      val expectedUtilities: Map[Action, Utility] = computeExpectedUtilities(c)
      val maxUtility = expectedUtilities.values.max
      val maxUtilityMoves: Seq[Action] = expectedUtilities.filter(_._2 == maxUtility).map(_._1).toSeq
      val numberOfMaxUtilityMoves = maxUtilityMoves.size
      //If we are converged already don't stir the boat
      //      if ((isConvergedGivenUtilitiesAndMaxUtility(c, expectedUtilities, maxUtility)) &&
      //        ((numberOfMaxUtilityMoves > 1 && c.computeExpectedNumberOfConflicts == 0)
      //          || numberOfMaxUtilityMoves == 1)) {
      //        c.centralVariableValue

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

  trait ExplorerArgmaxBDecisionRule extends ArgmaxADecisionRule {
    this: TargetFunction =>

    def expl: Double

    override def computeMove(c: Config) = {
      val expectedUtilities: Map[Action, Utility] = computeExpectedUtilities(c)
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

  trait SimulatedAnnealingDecisionRule extends ArgmaxADecisionRule {
    this: TargetFunction =>

    def const: Double
    def k: Double
    var iteration = 0  
      
    override def computeMove(c: Config) = {
      iteration +=1
      val randomMove = c.domain.toSeq(Random.nextInt(c.domain.size))
      val expectedUtilities = computeExpectedUtilities(c).toMap[Action, Double]
      val delta = expectedUtilities.getOrElse[Double](randomMove, -1) - expectedUtilities.getOrElse[Double](c.centralVariableValue, -1)
      if (delta <= 0 && Random.nextDouble <= scala.math.exp(delta/iteration)) {
        randomMove
      } else {
        c.centralVariableValue
      }
    }
  }

}
