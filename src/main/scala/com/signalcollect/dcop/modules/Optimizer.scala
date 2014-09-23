package com.signalcollect.dcop.modules

trait Optimizer[AgentId, Action, Config <: Configuration[AgentId, Action], UtilityType] {
  def schedule: AdjustmentSchedule[AgentId, Action, Config]
  def rule: DecisionRule[AgentId, Action, Config, UtilityType]

  def shouldConsiderMove(c: Config): Boolean = schedule.shouldConsiderMove(c)

  def computeMove(c: Config): Action = rule.computeMove(c)

  def isConverged(c: Config): Boolean = rule.isConverged(c)
}
