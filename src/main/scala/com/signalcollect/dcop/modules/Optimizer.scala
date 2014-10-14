package com.signalcollect.dcop.modules

trait Optimizer extends Serializable {
  type AgentId
  type Action
  type UtilityType
  type Config <: Configuration[AgentId, Action, UtilityType]
  def schedule: AdjustmentSchedule[AgentId, Action, Config]
  def rule: DecisionRule[AgentId, Action, Config] 
    with TargetFunction[AgentId, Action, Config, UtilityType] 
    with UtilityFunction[AgentId, Action, Config, UtilityType]
   // with Memory 

  def shouldConsiderMove(c: Config): Boolean = schedule.shouldConsiderMove(c)

  def computeMove(c: Config): Action = rule.computeMove(c)

  def shouldTerminate(c: Config): Boolean = rule.shouldTerminate(c)

  def isInLocalOptimum(c: Config): Boolean = rule.isInLocalOptimum(c)
  
  def updateMemory(c: Config): Config = rule.updateMemory(c)
  
  
  
}
