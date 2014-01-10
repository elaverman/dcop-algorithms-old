package com.signalcollect.dcop.impl

import com.signalcollect.dcop.modules._

trait TargetFunctionsWithUtilityFunctions[AgentId, Action] extends TargetFunctionModule[AgentId, Action] {
  this: UtilityFunctionModule[AgentId, Action] with ConfigurationModule[AgentId, Action] =>

  trait MemoryLessTargetFunction extends TargetFunction {
    this: UtilityFunction =>

    def computeExpectedUtilities(c: Config) = {
      val configurationCandidates: Set[Config] = for {
        assignment <- c.domain
      } yield c.withCentralVariableAssignment(assignment)
      val configUtilities = configurationCandidates.map(c => (c.centralVariableValue, computeUtility(c))).toMap
      configUtilities
    }

  }
}

trait RankedTargetFunctions[AgentId, Action] extends TargetFunctionModule[AgentId, Action] {
  this: RankedConfiguration[AgentId, Action] =>

  trait RankWeightedTargetFunction extends TargetFunction {
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
    
  }
}
