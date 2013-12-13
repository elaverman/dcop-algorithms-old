package com.signalcollect.dcop.modules

trait OptimizerModule[AgentId, Action]
  extends ConfigurationModule[AgentId, Action]
  with AdjustmentScheduleModule[AgentId, Action]
  with DecisionRuleModule[AgentId, Action] {

  def createNewConfig(params: Any*) = factory.createNewConfig(params: _*)

  def shouldConsiderMove(c: Config) = schedule.shouldConsiderMove(c)

  def computeMove(c: Config) = rule.computeMove(c)

  def isLocalOptimum(c: Config) = rule.isLocalOptimum(c)
}
