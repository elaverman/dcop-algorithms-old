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

  class FloodAdjustmentSchedule extends AdjustmentSchedule {
    def shouldConsiderMove(c: Config) = true
  }

}

trait RankedAdjustmentSchedules[AgentId, Action] extends AdjustmentScheduleModule[AgentId, Action] {
  this: RankedConfiguration[AgentId, Action] =>

  class RankedBasedAdjustmentSchedule(relativeChangeProbability: Double) extends AdjustmentSchedule {
    def shouldConsiderMove(c: Config) = {
      val maxNeighbourRank = c.ranks.values.max
      val rankForCurrentConfig = c.ranks(c.centralVariableAssignment._1)
      val relativeRankRatio = rankForCurrentConfig / maxNeighbourRank // Ranks are > 0
      val changeProbability = 1 - relativeRankRatio * relativeChangeProbability // The higher the rank ratio, the lower the probability to change.
      Random.nextDouble <= changeProbability
    }
  }

  class InvertRankedBasedAdjustmentSchedule(relativeChangeProbability: Double) extends AdjustmentSchedule {
    def shouldConsiderMove(c: Config) = {
      val maxNeighbourRank = c.ranks.values.max
      val rankForCurrentConfig = c.ranks(c.centralVariableAssignment._1)
      val relativeRankRatio = rankForCurrentConfig / maxNeighbourRank // Ranks are > 0
      val changeProbability = /*1 - */ relativeRankRatio * relativeChangeProbability // The higher the rank ratio, the lower the probability to change.
      Random.nextDouble <= changeProbability
    }
  }

  //TODO: Finish this and test
//  class DiscountedRankedBasedAdjustmentSchedule(baseChangeProbability: Double) extends AdjustmentSchedule {
//    var stepCount: Int = 0 
//    
//    def shouldConsiderMove(c: Config) = {
//      val maxNeighbourRank = c.ranks.values.max
//      val rankForCurrentConfig = c.ranks(c.centralVariableAssignment._1)
//      val relativeRankRatio = rankForCurrentConfig / maxNeighbourRank // Ranks are > 0
//      val changeProbability = baseChangeProbability - relativeRankRatio * relativeChangeProbability // The higher the rank ratio, the lower the probability to change.
//      Random.nextDouble <= changeProbability
//    }
//  }

}


