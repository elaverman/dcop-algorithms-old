package com.signalcollect.dcop.modules

//TODO Config <: Configuration[AgentId, Action]?
trait TargetFunction[AgentId, Action, SignalType, Config <: Configuration[AgentId, Action, SignalType], UtilityType] extends Serializable {
  def computeExpectedUtilities(c: Config): Map[Action, UtilityType]
  def updateMemory(c: Config): Config
}
