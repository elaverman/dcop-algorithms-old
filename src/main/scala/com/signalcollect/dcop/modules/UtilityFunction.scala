package com.signalcollect.dcop.modules


trait UtilityFunction[AgentId, Action, SignalType, Config <: Configuration[AgentId, Action, SignalType], UtilityType] extends Serializable {
  def computeUtility(c: Config): UtilityType
}
