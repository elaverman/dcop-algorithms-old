package com.signalcollect.dcop.modules

trait TargetFunction[AgentId, Action, Config, UtilityType] extends Serializable {
  def computeExpectedUtilities(c: Config): Map[Action, UtilityType]
}
