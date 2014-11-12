package com.signalcollect.dcop.modules

//TODO: type Config with AgentId and Action
trait AdjustmentSchedule[AgentId, Action, SignalType, Config <: Configuration[AgentId, Action, SignalType]] extends Serializable {
  def shouldConsiderMove(c: Config): Boolean
}

