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

  trait ArgmaxADecisionRule extends DecisionRule {
    this: TargetFunction =>

    def computeMove(c: Config) = {
      val expectedUtilities: Map[Action, Utility] = computeExpectedUtilities(c)
      val maxUtility = expectedUtilities.values.max
      if (isLocalOptimumGivenUtilitiesAndMaxUtility(c, expectedUtilities, maxUtility)) {
        c.centralVariableValue
      } else {
        val maxUtilityMoves: Seq[Action] = expectedUtilities.filter(_._2 == maxUtility).map(_._1).toSeq
        val chosenMaxUtilityMove = maxUtilityMoves(Random.nextInt(maxUtilityMoves.size))
        chosenMaxUtilityMove
      }
    }

    // TODO: move to special DecisionRule trait with a default implementation.
    protected def isLocalOptimumGivenUtilitiesAndMaxUtility(
      c: Config,
      expectedUtilities: Map[Action, Utility],
      maxUtility: Utility): Boolean = {
      val currentUtility = expectedUtilities(c.centralVariableValue)
      maxUtility == currentUtility
    }

    def isLocalOptimum(c: Config): Boolean = {
      val expectedUtilities: Map[Action, Utility] = computeExpectedUtilities(c)
      val maxUtility = expectedUtilities.values.max
      isLocalOptimumGivenUtilitiesAndMaxUtility(c, expectedUtilities, maxUtility)
    }

  }

  trait ArgmaxBDecisionRule extends ArgmaxADecisionRule {
    this: TargetFunction =>

    override def computeMove(c: Config) = {
      val expectedUtilities: Map[Action, Utility] = computeExpectedUtilities(c)
      val maxUtility = expectedUtilities.values.max
      val maxUtilityMoves: Seq[Action] = expectedUtilities.filter(_._2 == maxUtility).map(_._1).toSeq
      val numberOfMaxUtilityMoves = maxUtilityMoves.size
      if ((isLocalOptimumGivenUtilitiesAndMaxUtility(c, expectedUtilities, maxUtility)) && ((numberOfMaxUtilityMoves > 1 && c.computeExpectedNumberOfConflicts == 0) || numberOfMaxUtilityMoves == 1)) {
        c.centralVariableValue
      } else {
        val chosenMaxUtilityMove = maxUtilityMoves(Random.nextInt(maxUtilityMoves.size))
        chosenMaxUtilityMove
      }
    }
  }

  
}
