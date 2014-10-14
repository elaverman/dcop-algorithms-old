package com.signalcollect.dcop.modules

//TODO: type Config with AgentId and Action
trait AdjustmentSchedule extends Serializable {
  this => Optimizer
  def shouldConsiderMove(c: Config): Boolean
}

