package com.signalcollect.dcop.impl

import com.signalcollect.dcop.modules._
import scala.util.Random

trait MemoryLessTargetFunction[AgentId, Action, Config <: Configuration[AgentId, Action], UtilityType] extends TargetFunction[AgentId, Action, Config, UtilityType] with UtilityFunction[AgentId, Action, Config, UtilityType] {

  def computeExpectedUtilities(c: Config) = {
    val configurationCandidates = for {
      assignment <- c.domain
    } yield c.withCentralVariableAssignment(assignment)
    val configUtilities = configurationCandidates.map(c => (c.centralVariableValue, computeUtility(c))).toMap
    configUtilities
  }
}

/**
 * RankedTargetFunctions
 */

//TODO: Push the Utility calculation into UtilityFunctions.scala and replace the Double in the TargetFunction with the UtilityType.
trait RankWeightedTargetFunction[AgentId, Action, Config <: RankedConfiguration[AgentId, Action], UtilityType] extends TargetFunction[AgentId, Action, Config, Double] with UtilityFunction[AgentId, Action, Config, UtilityType] {

  def computeExpectedUtilities(c: Config) = {
    val configurationCandidates: Set[Config] = for {
      assignment <- c.domain
    } yield c.withCentralVariableAssignment(assignment)
    val configUtilities = configurationCandidates.map(configuration => {
      val (allies, opponents) = configuration.neighborhood.partition(_._2 != configuration.centralVariableValue)
      val allyRanks = allies.keys.map(c.ranks(_)).sum
      val opponentRanks = opponents.keys.map(c.ranks(_)).sum
      val expectedUtility = allyRanks - opponentRanks
      val expectedMoveUtility = (configuration.centralVariableValue, expectedUtility)
      //if (configuration.centralVariableAssignment._1 == 2) {
      //println(s"Expected move utility for agent ${configuration.centralVariableAssignment._1} and move ${expectedMoveUtility._1} is ${expectedMoveUtility._2}")
      //}
      expectedMoveUtility
    })
    configUtilities.toMap
  }

  /**
   * Same as RankWeightedTargetFunction, but when it reaches a NE it behaves like the MemoryLessTargetFunction
   */
  //TODO Push the Utility calculation into UtilityFunctions.scala and replace the Double in the RankWeightedTargetFunction with the UtilityType.
  trait DynamicRankWeightedTargetFunction[AgentId, Action, Config <: RankedConfiguration[AgentId, Action], UtilityType] extends RankWeightedTargetFunction[AgentId, Action, Config, Double] {

    def isAtRankedNashEquilibrium(c: Config): Boolean = {
      val expectedUtilities = computeRankedExpectedUtilities(c)
      val maxUtility = expectedUtilities.values.max
      val currentUtility = expectedUtilities(c.centralVariableValue)
      maxUtility == currentUtility
    }

    def computeRankedExpectedUtilities(c: Config) = super.computeExpectedUtilities(c)

    override def computeExpectedUtilities(c: Config) = {
      if (!isAtRankedNashEquilibrium(c)) {
        computeRankedExpectedUtilities(c)
      } else {
        val configurationCandidates: Set[Config] = for {
          assignment <- c.domain
        } yield c.withCentralVariableAssignment(assignment)
        val configUtilities = configurationCandidates.map(c => (c.centralVariableValue, computeUtility(c))).toMap
        configUtilities
      }
    }
  }

  /**
   * Same as RankWeightedTargetFunction, but when it reaches a certain iteration it behaves like the MemoryLessTargetFunction
   */
  trait Switch1RankWeightedTargetFunction[AgentId, Action, Config <: RankedConfiguration[AgentId, Action], UtilityType] extends RankWeightedTargetFunction[AgentId, Action, Config, Double] {

    var iteration = 0
    var switched = false

    def computeRankedExpectedUtilities(c: Config) = super.computeExpectedUtilities(c)

    override def computeExpectedUtilities(c: Config) = {
      iteration += 1
      if (switched == false && iteration > 10)
        if (Random.nextDouble <= 0.2)
          switched = true

      if (!switched) {
        computeRankedExpectedUtilities(c)
      } else {
        val configurationCandidates: Set[Config] = for {
          assignment <- c.domain
        } yield c.withCentralVariableAssignment(assignment)
        val configUtilities = configurationCandidates.map(c => (c.centralVariableValue, computeUtility(c))).toMap
        configUtilities
      }
    }
  }

  /**
   * Same as RankWeightedTargetFunction, but when it reaches a certain iteration it behaves like the MemoryLessTargetFunction
   */
  trait Switch2RankWeightedTargetFunction[AgentId, Action, Config <: RankedConfiguration[AgentId, Action], UtilityType] extends RankWeightedTargetFunction[AgentId, Action, Config, Double] {

    var iteration = 0
    var switched = false

    def computeRankedExpectedUtilities(c: Config) = super.computeExpectedUtilities(c)

    override def computeExpectedUtilities(c: Config) = {
      iteration += 1
      if (switched == false && iteration > 15)
        if (Random.nextDouble <= 0.2)
          switched = true

      if (!switched) {
        computeRankedExpectedUtilities(c)
      } else {
        val configurationCandidates: Set[Config] = for {
          assignment <- c.domain
        } yield c.withCentralVariableAssignment(assignment)
        val configUtilities = configurationCandidates.map(c => (c.centralVariableValue, computeUtility(c))).toMap
        configUtilities
      }
    }
  }

  /**
   * Same as RankWeightedTargetFunction, but when it reaches a certain iteration it behaves like the MemoryLessTargetFunction
   */
  trait Switch3RankWeightedTargetFunction[AgentId, Action, Config <: RankedConfiguration[AgentId, Action], UtilityType] extends RankWeightedTargetFunction[AgentId, Action, Config, Double] {

    var iteration = 0
    var switched = false

    def computeRankedExpectedUtilities(c: Config) = super.computeExpectedUtilities(c)

    override def computeExpectedUtilities(c: Config) = {
      iteration += 1
      if (switched == false && iteration > 20)
        if (Random.nextDouble <= 0.2)
          switched = true

      if (!switched) {
        computeRankedExpectedUtilities(c)
      } else {
        val configurationCandidates: Set[Config] = for {
          assignment <- c.domain
        } yield c.withCentralVariableAssignment(assignment)
        val configUtilities = configurationCandidates.map(c => (c.centralVariableValue, computeUtility(c))).toMap
        configUtilities
      }
    }
  }

  /**
   * Same as MemoryLessTargetFunction, but when it reaches a certain iteration it behaves like the RankWeightedTargetFunction
   */
  trait SwitchInv1RankWeightedTargetFunction[AgentId, Action, Config <: RankedConfiguration[AgentId, Action], UtilityType] extends RankWeightedTargetFunction[AgentId, Action, Config, Double] {

    var iteration = 0
    var switched = false

    def computeRankedExpectedUtilities(c: Config) = super.computeExpectedUtilities(c)

    override def computeExpectedUtilities(c: Config) = {
      iteration += 1
      if (switched == false && iteration > 10)
        if (Random.nextDouble <= 0.2)
          switched = true

      if (!switched) {
        computeRankedExpectedUtilities(c)
      } else {
        val configurationCandidates: Set[Config] = for {
          assignment <- c.domain
        } yield c.withCentralVariableAssignment(assignment)
        val configUtilities = configurationCandidates.map(c => (c.centralVariableValue, computeUtility(c))).toMap
        configUtilities
      }
    }
  }

  /**
   * Same as MemoryLessTargetFunction, but when it reaches a certain iteration it behaves like the RankWeightedTargetFunction
   */
  trait SwitchInv2RankWeightedTargetFunction[AgentId, Action, Config <: RankedConfiguration[AgentId, Action], UtilityType] extends RankWeightedTargetFunction[AgentId, Action, Config, Double] {

    var iteration = 0
    var switched = false

    def computeRankedExpectedUtilities(c: Config) = super.computeExpectedUtilities(c)

    override def computeExpectedUtilities(c: Config) = {
      iteration += 1
      if (switched == false && iteration > 15)
        if (Random.nextDouble <= 0.2)
          switched = true

      if (!switched) {
        computeRankedExpectedUtilities(c)
      } else {
        val configurationCandidates: Set[Config] = for {
          assignment <- c.domain
        } yield c.withCentralVariableAssignment(assignment)
        val configUtilities = configurationCandidates.map(c => (c.centralVariableValue, computeUtility(c))).toMap
        configUtilities
      }
    }
  }

  /**
   * Same as MemoryLessTargetFunction, but when it reaches a certain iteration it behaves like the RankWeightedTargetFunction
   */
  trait SwitchInv3RankWeightedTargetFunction[AgentId, Action, Config <: RankedConfiguration[AgentId, Action], UtilityType] extends RankWeightedTargetFunction[AgentId, Action, Config, Double] {

    var iteration = 0
    var switched = false

    def computeRankedExpectedUtilities(c: Config) = super.computeExpectedUtilities(c)

    override def computeExpectedUtilities(c: Config) = {
      iteration += 1
      if (switched == false && iteration > 20)
        if (Random.nextDouble <= 0.2)
          switched = true

      if (!switched) {
        computeRankedExpectedUtilities(c)
      } else {
        val configurationCandidates: Set[Config] = for {
          assignment <- c.domain
        } yield c.withCentralVariableAssignment(assignment)
        val configUtilities = configurationCandidates.map(c => (c.centralVariableValue, computeUtility(c))).toMap
        configUtilities
      }
    }
  }

}
