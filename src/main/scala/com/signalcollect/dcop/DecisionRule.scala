/*
 *  @author Philip Stutz
 *  @author Mihaela Verman
 *  
 *  Copyright 2013 University of Zurich
 *      
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *         http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 */

package com.signalcollect.dcop

import scala.util.Random

trait ConfigEvaluator[VariableType, ValueType, C <: Configuration[VariableType, ValueType]] {
  type Config = C
  def utility(c: Config): Double
  def utility(a: ValueType, b: ValueType): Double
}

trait VertexColoringConfigEvaluator extends ConfigEvaluator[Int, Int, Configuration[Int, Int]] {
  def utility(c: Config): Double = {
    val occupiedColors = c.neighborhood.values
    val numberOfConflicts = occupiedColors.filter(_ == c.centralVariableValue).size
    val numberOfNeighbors = occupiedColors.size
    val neighborsInSync = numberOfNeighbors - numberOfConflicts
    neighborsInSync
  }
  //Utility for only one binary constraint
  def utility(centralVariableValue: Int, neighborVariableValue: Int): Double = {
    if (centralVariableValue == neighborVariableValue) 0
    else 1
  }
}

trait Configuration[VariableIdType, ValueType] {
  def neighborhood: Map[VariableIdType, ValueType]
  def domain: Set[ValueType]
  def withCentralVariableAssignment(value: ValueType): Configuration[VariableIdType, ValueType]
  def centralVariableAssignment: (VariableIdType, ValueType)
  def centralVariableValue = centralVariableAssignment._2
}

trait RankedConfiguration[VariableIdType, ValueType] extends Configuration[VariableIdType, ValueType] {
  def rank: Double
}

case class VertexColoringConfig(
  val neighborhood: Map[Int, Int],
  val domain: Set[Int],
  val centralVariableAssignment: (Int, Int)) extends Configuration[Int, Int] {
  def withCentralVariableAssignment(value: Int): Configuration[Int, Int] = {
    this.copy(centralVariableAssignment = (centralVariableAssignment._1, value))
  }
}

case class RankedVertexColoringConfig(
  val neighborhood: Map[Int, Int],
  val neighborhoodRanks: Map[Int, Double],
  val domain: Set[Int],
  val centralVariableAssignment: (Int, Int),
  val centralVariableRank: Double, 
  val dampingFactor: Double) extends RankedConfiguration[Int, Int] {

  //TODO: move rank somewhere else... WHERE!?!
//  def newRank = {
//    1 - dampingFactor + dampingFactor * ((neighborhood map (x => utility(centralVariableAssignment, x._2) * neighborhoodRanks.getOrElse(x._1, 0))).sum)
//  }
  
  def withCentralVariableAssignment(value: Int): Configuration[Int, Int] = {
    this.copy(centralVariableAssignment = (centralVariableAssignment._1, value))
  }
}

trait DecisionRule[VariableType, ValueType] extends ConfigEvaluator[VariableType, ValueType, Configuration[VariableType, ValueType]] {
  def computeNewAssignment(currentConfiguration: Config): ValueType
}

trait AdjustmentSchedule[VariableType, ValueType, C <: Configuration[VariableType, ValueType]] extends ConfigEvaluator[VariableType, ValueType, Configuration[VariableType, ValueType]] {
  def scheduleAssignment(currentConfiguration: Config): ValueType
}

trait FloodAdjustmentSchedule[VariableType, ValueType, C <: Configuration[VariableType, ValueType]]
  extends AdjustmentSchedule[VariableType, ValueType, C]
  with DecisionRule[VariableType, ValueType] {
  def scheduleAssignment(currentConfiguration: Config): ValueType = {
    computeNewAssignment(currentConfiguration)
  }
}

trait ParallelRandomAdjustmentSchedule[VariableType, ValueType, C <: Configuration[VariableType, ValueType]]
  extends AdjustmentSchedule[VariableType, ValueType, C]
  with DecisionRule[VariableType, ValueType] {
  def changeProbability: Double
  def scheduleAssignment(currentConfiguration: Config): ValueType = {
    if (Random.nextDouble <= changeProbability) {
      computeNewAssignment(currentConfiguration)
    } else {
      currentConfiguration.centralVariableValue
    }
  }
}

//Assumes that the rank is scaled to [0,1]
trait PageRankAdjustmentSchedule[VariableType, ValueType, C <: Configuration[VariableType, ValueType]]
  extends AdjustmentSchedule[VariableType, ValueType, C]
  with DecisionRule[VariableType, ValueType] {

  def scheduleAssignment(currentConfiguration: Config, scaledPageRankFactor: Double): ValueType = {
    if (Random.nextDouble <= scaledPageRankFactor) {
      computeNewAssignment(currentConfiguration)
    } else {
      currentConfiguration.centralVariableValue
    }
  }
}

trait ArgmaxA extends DecisionRule[Int, Int] {
  def changeThreshold: Double
  def computeNewAssignment(currentConfiguration: Config): Int = {
    val configurationCandidates: Set[Config] = for {
      assignment <- currentConfiguration.domain
    } yield currentConfiguration.withCentralVariableAssignment(assignment)
  
    val configUtilities: Map[Config, Double] = configurationCandidates.map(c => (c, utility(c))).toMap
    val maxUtility = configUtilities.values.max
    val maxUtilityConfigs: Seq[Config] = configUtilities.filter(_._2 == maxUtility).map(_._1).toSeq
    val currentUtility = configUtilities(currentConfiguration)
    val chosenMaxUtilityConfig = maxUtilityConfigs(Random.nextInt(maxUtilityConfigs.size))
    
    chosenMaxUtilityConfig.centralVariableValue
  }
}

case class ColoringDsaA(val changeProbability: Double) extends ArgmaxA with VertexColoringConfigEvaluator with ParallelRandomAdjustmentSchedule[Int, Int, VertexColoringConfig]

case class RankedDsaA extends ArgmaxA with VertexColoringConfigEvaluator with PageRankAdjustmentSchedule[Int, Int, RankedVertexColoringConfig]
//  def changeThreshold = {
//    0
//
//    ???
//  }
//}

