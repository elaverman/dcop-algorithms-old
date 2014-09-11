package com.signalcollect.dcop.modules

trait AdjustmentScheduleModule[AgentId, Action, ConstraintParams] {
  this: ConfigurationModule[AgentId, Action, ConstraintParams] =>

  val schedule: AdjustmentSchedule

  trait AdjustmentSchedule extends Serializable {
    def shouldConsiderMove(c: Config): Boolean
  }
}
