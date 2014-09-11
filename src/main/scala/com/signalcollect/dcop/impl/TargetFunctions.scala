package com.signalcollect.dcop.impl

import com.signalcollect.dcop.modules._
import scala.util.Random

trait TargetFunctionsWithUtilityFunctions[AgentId, Action, ConstraintParams] extends TargetFunctionModule[AgentId, Action, ConstraintParams] {
  this: UtilityFunctionModule[AgentId, Action, ConstraintParams] with ConfigurationModule[AgentId, Action, ConstraintParams] =>

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

trait RankedTargetFunctions[AgentId, Action, ConstraintParams] extends TargetFunctionModule[AgentId, Action, ConstraintParams] {
  this: UtilityFunctionModule[AgentId, Action, ConstraintParams] with RankedConfiguration[AgentId, Action, ConstraintParams] =>

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

  /**
   * Same as RankWeightedTargetFunction, but when it reaches a NE it behaves like the MemoryLessTargetFunction
   */
  trait DynamicRankWeightedTargetFunction extends RankWeightedTargetFunction {
    this: UtilityFunction =>

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
  trait Switch1RankWeightedTargetFunction extends RankWeightedTargetFunction {
    this: UtilityFunction =>

    var iteration = 0  
    var switched = false
      
//    def isAtRankedNashEquilibrium(c: Config): Boolean = {
//      val expectedUtilities = computeRankedExpectedUtilities(c)
//      val maxUtility = expectedUtilities.values.max
//      val currentUtility = expectedUtilities(c.centralVariableValue)
//      maxUtility == currentUtility
//    }
      
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
  trait Switch2RankWeightedTargetFunction extends RankWeightedTargetFunction {
    this: UtilityFunction =>

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
  trait Switch3RankWeightedTargetFunction extends RankWeightedTargetFunction {
    this: UtilityFunction =>

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
  trait SwitchInv1RankWeightedTargetFunction extends RankWeightedTargetFunction {
    this: UtilityFunction =>

    var iteration = 0  
    var switched = false
    
      
//    def isAtRankedNashEquilibrium(c: Config): Boolean = {
//      val expectedUtilities = computeRankedExpectedUtilities(c)
//      val maxUtility = expectedUtilities.values.max
//      val currentUtility = expectedUtilities(c.centralVariableValue)
//      maxUtility == currentUtility
//    }
      
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
  trait SwitchInv2RankWeightedTargetFunction extends RankWeightedTargetFunction {
    this: UtilityFunction =>

    var iteration = 0  
    var switched = false
    
      
//    def isAtRankedNashEquilibrium(c: Config): Boolean = {
//      val expectedUtilities = computeRankedExpectedUtilities(c)
//      val maxUtility = expectedUtilities.values.max
//      val currentUtility = expectedUtilities(c.centralVariableValue)
//      maxUtility == currentUtility
//    }
      
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
  trait SwitchInv3RankWeightedTargetFunction extends RankWeightedTargetFunction {
    this: UtilityFunction =>

    var iteration = 0  
    var switched = false
    
      
//    def isAtRankedNashEquilibrium(c: Config): Boolean = {
//      val expectedUtilities = computeRankedExpectedUtilities(c)
//      val maxUtility = expectedUtilities.values.max
//      val currentUtility = expectedUtilities(c.centralVariableValue)
//      maxUtility == currentUtility
//    }
      
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
