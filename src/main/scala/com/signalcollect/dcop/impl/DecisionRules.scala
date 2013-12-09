package com.signalcollect.dcop.impl

import scala.util.Random
import com.signalcollect.dcop.modules._


trait DecisionRules[AgentId, Action] extends DecisionRuleModule[AgentId, Action] {
  this: ConfigurationModule[AgentId, Action] =>

    object RandomDecisionRule extends DecisionRule {
       def computeMove(c: Config) = {
         val randomMove: Action = c.domain.toSeq(Random.nextInt(c.domain.size))
         randomMove
       }
    }
}


trait DecisionRulesWithTargetFunctions[AgentId, Action] extends DecisionRuleModule[AgentId, Action] {
  this: TargetFunctionModule[AgentId, Action] with ConfigurationModule[AgentId, Action] =>

  trait ArgmaxADecisionRule extends DecisionRule {
    this: TargetFunction =>

    def computeMove(c: Config) = {
      val expectedUtilities: Map[Action, Utility] = computeExpectedUtilities(c)
      val maxUtility = expectedUtilities.values.max
      val currentUtility = expectedUtilities(c.centralVariableValue)
      if (maxUtility > currentUtility) {
        val maxUtilityMoves: Seq[Action] = expectedUtilities.filter(_._2 == maxUtility).map(_._1).toSeq
        val chosenMaxUtilityMove = maxUtilityMoves(Random.nextInt(maxUtilityMoves.size))
        chosenMaxUtilityMove
      } else {
        c.centralVariableValue
      }
    }
  }
}
