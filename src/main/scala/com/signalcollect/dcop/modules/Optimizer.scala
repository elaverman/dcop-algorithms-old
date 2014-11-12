package com.signalcollect.dcop.modules

trait Optimizer[AgentId, Action, SignalType, Config <: Configuration[AgentId, Action, SignalType], UtilityType] extends Serializable {
  def schedule: AdjustmentSchedule[AgentId, Action, SignalType, Config]
  def rule: DecisionRule[AgentId, Action, SignalType, Config] 
    with TargetFunction[AgentId, Action, SignalType, Config, UtilityType] 
    with UtilityFunction[AgentId, Action, SignalType, Config, UtilityType]
   // with Memory 

  def shouldConsiderMove(c: Config): Boolean = schedule.shouldConsiderMove(c)

  def computeMove(c: Config): Action = rule.computeMove(c)

  def shouldTerminate(c: Config): Boolean = rule.shouldTerminate(c)

  def isInLocalOptimum(c: Config): Boolean = rule.isInLocalOptimum(c)
  
  def updateMemory(c: Config): Config = rule.updateMemory(c)
  
  
  
}
