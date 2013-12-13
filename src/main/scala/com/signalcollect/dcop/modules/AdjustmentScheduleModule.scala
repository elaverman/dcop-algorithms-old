package com.signalcollect.dcop.modules

trait AdjustmentScheduleModule[AgentId, Action] {
  this: ConfigurationModule[AgentId, Action] =>

  val schedule: AdjustmentSchedule

  trait AdjustmentSchedule {
    def shouldConsiderMove(c: Config): Boolean
  }
}
