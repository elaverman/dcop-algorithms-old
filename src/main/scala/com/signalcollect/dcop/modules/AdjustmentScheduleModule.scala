package com.signalcollect.dcop.modules

trait AdjustmentScheduleModule[AgentId, Action] {
  this: ConfigurationModule[AgentId, Action] =>
    
  trait AdjustmentSchedule {
    def apply(c: Config) = shouldConsiderMove(c)
    def shouldConsiderMove(c: Config): Boolean
  }
}
