package com.signalcollect.dcop.impl

import scala.util.Random
import com.signalcollect.dcop.modules._

trait AdjustmentSchedules[AgentId, Action] extends AdjustmentScheduleModule[AgentId, Action] {
  this: ConfigurationModule[AgentId, Action] =>

  class ParallelRandomAdjustmentSchedule(changeProbability: Double) extends AdjustmentSchedule {
    def shouldConsiderMove(c: Config) = {
      Random.nextDouble <= changeProbability
    }
  }

  class ParallelRandomAdjustmentScheduleWithPageRankSupport(changeProbability: Double) extends AdjustmentSchedule {
    def shouldConsiderMove(c: Config) = {
      Random.nextDouble <= changeProbability
    }
  }
}
