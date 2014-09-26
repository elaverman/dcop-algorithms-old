package com.signalcollect.dcop.modules

//TODO Config <: Configuration[AgentId, Action]?
trait TargetFunction[AgentId, Action, Config, UtilityType] extends Serializable {
  def computeExpectedUtilities(c: Config): Map[Action, UtilityType]
}
