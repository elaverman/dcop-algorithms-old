package com.signalcollect.dcop.impl

import scala.util.Random
import com.signalcollect.dcop.modules._

//TODO Constrain Config <: Configuration[AgentId, Action]?
class ParallelRandomAdjustmentSchedule[AgentId, Action, Config](changeProbability: Double) extends AdjustmentSchedule[AgentId, Action, Config] {
  def shouldConsiderMove(c: Config) = {
    Random.nextDouble <= changeProbability
  }
}

class FloodAdjustmentSchedule[AgentId, Action, Config] extends AdjustmentSchedule[AgentId, Action, Config] {
  def shouldConsiderMove(c: Config) = true
}

class RankedBasedAdjustmentSchedule[AgentId, Action, Config <: RankedConfiguration[AgentId, Action]](relativeChangeProbability: Double) extends AdjustmentSchedule[AgentId, Action, Config] { //with UtilityFunction[AgentId, Action, Config]{
  def shouldConsiderMove(c: Config) = {
    val maxNeighbourRank = c.ranks.values.max
    val rankForCurrentConfig = c.ranks(c.centralVariableAssignment._1)
    val relativeRankRatio = rankForCurrentConfig / maxNeighbourRank
    val changeProbability = 1 - relativeRankRatio * relativeChangeProbability // The higher the rank ratio, the lower the probability to change.
    Random.nextDouble <= changeProbability
  }
}

class InvertRankedBasedAdjustmentSchedule[AgentId, Action, Config <: RankedConfiguration[AgentId, Action]](relativeChangeProbability: Double) extends AdjustmentSchedule[AgentId, Action, Config] {
  def shouldConsiderMove(c: Config) = {
    val maxNeighbourRank = c.ranks.values.max
    val rankForCurrentConfig = c.ranks(c.centralVariableAssignment._1)
    val relativeRankRatio = rankForCurrentConfig / maxNeighbourRank
    val changeProbability = relativeRankRatio * relativeChangeProbability // The higher the rank ratio, the higher the probability to change.
    Random.nextDouble <= changeProbability
  }
}

class DynamicRankedBasedAdjustmentSchedule[AgentId, Action, Config <: RankedConfiguration[AgentId, Action]](relativeChangeProbability: Double) extends AdjustmentSchedule[AgentId, Action, Config] {

  def isAtRankedNashEquilibriumInAdjustmentSchedule(c: Config): Boolean = {
    val expectedUtilities = computeRankedExpectedUtilitiesInAdjustmentSchedule(c)
    val maxUtility = expectedUtilities.values.max
    val currentUtility = expectedUtilities(c.centralVariableValue)
    maxUtility == currentUtility
  }

  def computeRankedExpectedUtilitiesInAdjustmentSchedule(c: Config) = {
    val configurationCandidates = for {
      assignment <- c.domain
    } yield c.withCentralVariableAssignment(assignment)
    val configUtilities = configurationCandidates.map(configuration => {
      val (allies, opponents) = configuration.neighborhood.partition(_._2 != configuration.centralVariableValue)
      val allyRanks = allies.keys.map(c.ranks(_)).sum
      val opponentRanks = opponents.keys.map(c.ranks(_)).sum
      val expectedUtility = allyRanks - opponentRanks
      val expectedMoveUtility = (configuration.centralVariableValue, expectedUtility)
      expectedMoveUtility
    })
    configUtilities.toMap
  }

  def shouldConsiderMove(c: Config) = {
    val maxNeighbourRank = c.ranks.values.max
    val rankForCurrentConfig = c.ranks(c.centralVariableAssignment._1)
    val relativeRankRatio = rankForCurrentConfig / maxNeighbourRank
    val changeProbability = (if (!isAtRankedNashEquilibriumInAdjustmentSchedule(c)) 1 - relativeRankRatio else relativeRankRatio) * relativeChangeProbability // The higher the rank ratio, the higher the probability to change.
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




