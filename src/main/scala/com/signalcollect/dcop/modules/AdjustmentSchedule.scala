package com.signalcollect.dcop.modules

//TODO: type Config with AgentId and Action
trait AdjustmentSchedule[AgentId, Action, Config] extends Serializable {
  def shouldConsiderMove(c: Config): Boolean
}

