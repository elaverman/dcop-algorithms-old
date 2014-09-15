package com.signalcollect.dcop.modules

trait Optimizer[AgentId, Action, +Config <: Configuration[AgentId, Action], UtilityType] {
  def schedule: AdjustmentSchedule[AgentId, Action, Config]
  def rule: DecisionRule[AgentId, Action, Config, UtilityType]

  def shouldConsiderMove(c: Config) = schedule.shouldConsiderMove(c)

  def computeMove(c: Config) = rule.computeMove(c)

  def isConverged(c: Config) = rule.isConverged(c)
}
