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

    override def computeMove(c: Config) = {
      val expectedUtilities: Map[Action, Utility] = computeExpectedUtilities(c)
      val maxUtility = expectedUtilities.values.max
      val maxUtilityMoves: Seq[Action] = expectedUtilities.filter(_._2 == maxUtility).map(_._1).toSeq
      val numberOfMaxUtilityMoves = maxUtilityMoves.size
      //If we are converged already don't stir the boat
      if ((isConvergedGivenUtilitiesAndMaxUtility(c, expectedUtilities, maxUtility)) &&
        ((numberOfMaxUtilityMoves > 1 && c.computeExpectedNumberOfConflicts == 0)
          || numberOfMaxUtilityMoves == 1)) {
        c.centralVariableValue
      } else {
        val chosenMaxUtilityMove = maxUtilityMoves(Random.nextInt(maxUtilityMoves.size))
        chosenMaxUtilityMove
      }
    }
  }

}
