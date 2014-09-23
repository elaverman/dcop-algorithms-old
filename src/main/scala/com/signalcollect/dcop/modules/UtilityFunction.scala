package com.signalcollect.dcop.modules


trait UtilityFunction[AgentId, Action, Config <: Configuration[AgentId, Action], UtilityType] extends Serializable {
  def computeUtility(c: Config): UtilityType
}
