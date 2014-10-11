package com.signalcollect.dcop.modules

trait Optimizer[AgentId, Action, Config <: Configuration[AgentId, Action], UtilityType] extends Serializable {
  def schedule: AdjustmentSchedule[AgentId, Action, Config]
  def rule: DecisionRule[AgentId, Action, Config] with TargetFunction[AgentId, Action, Config, UtilityType] with UtilityFunction[AgentId, Action, Config, UtilityType]

  def shouldConsiderMove(c: Config): Boolean = schedule.shouldConsiderMove(c)

  def computeMove(c: Config): Action = rule.computeMove(c)

  def shouldTerminate(c: Config): Boolean = rule.shouldTerminate(c)

  def isInLocalOptimum(c: Config): Boolean = rule.isInLocalOptimum(c)
}
