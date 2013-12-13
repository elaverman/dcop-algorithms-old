package com.signalcollect.dcop.modules

trait DecisionRuleModule[AgentId, Action] {
  this: ConfigurationModule[AgentId, Action] =>

  type Utility = Double

  val rule: DecisionRule
  
  trait DecisionRule {
    def computeMove(c: Config): Action
    def isLocalOptimum(c: Config): Boolean
  }
}
