package com.signalcollect.dcop.modules

//TODO Config <: Configuration[AgentId, Action]?
trait TargetFunction[AgentId, Action, Config <: Configuration[AgentId, Action], UtilityType] extends Serializable {
  def computeExpectedUtilities(c: Config): Map[Action, UtilityType]
  def updateMemory(c: Config): Config
}
