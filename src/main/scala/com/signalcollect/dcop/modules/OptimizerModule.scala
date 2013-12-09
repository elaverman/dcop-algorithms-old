package com.signalcollect.dcop.modules

trait OptimizerModule[AgentId, Action]
  extends ConfigurationModule[AgentId, Action]
  with AdjustmentScheduleModule[AgentId, Action]
  with DecisionRuleModule[AgentId, Action] {
  def createConfig: ConfigFactory
  def shouldConsiderMove: AdjustmentSchedule
  def computeMove: DecisionRule
  def isLocalOptimum(c: Config): Boolean
}
