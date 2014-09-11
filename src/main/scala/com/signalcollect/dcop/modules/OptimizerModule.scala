package com.signalcollect.dcop.modules

trait OptimizerModule[AgentId, Action, ConstraintParams]
  extends ConfigurationModule[AgentId, Action, ConstraintParams]
  with AdjustmentScheduleModule[AgentId, Action, ConstraintParams]
  with DecisionRuleModule[AgentId, Action, ConstraintParams] {

  def createNewConfig(params: Any*) = factory.createNewConfig(params: _*)

  def shouldConsiderMove(c: Config) = schedule.shouldConsiderMove(c)

  def computeMove(c: Config) = rule.computeMove(c)

  def isConverged(c: Config) = rule.isConverged(c)
}
