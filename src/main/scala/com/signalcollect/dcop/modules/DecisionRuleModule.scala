package com.signalcollect.dcop.modules

trait DecisionRuleModule[AgentId, Action] {
  this: ConfigurationModule[AgentId, Action] =>

  type Utility = Double

  trait DecisionRule {
    def apply(c: Config) = computeMove(c)
    def computeMove(c: Config): Action
  }
}
