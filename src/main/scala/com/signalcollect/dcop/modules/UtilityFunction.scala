package com.signalcollect.dcop.modules

//TODO: would this be better? Config <: Configuration[AgentId, Action]

trait UtilityFunction[AgentId, Action, Config, UtilityType] extends Serializable {
  def computeUtility(c: Config): UtilityType
}
