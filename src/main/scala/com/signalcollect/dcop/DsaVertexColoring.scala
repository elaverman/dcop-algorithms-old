package com.signalcollect.dcop

import scala.util.Random

class DsaVertexColoring(changeProbability: Double) extends OptimizerModule[Int, Int] with DefaultConfigurationModule[Int, Int] {

  val utility = new VertexColoringUtility
  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
  val target = new MemoryLessTargetFunction
  val rule = new ArgmaxA
  val factory = new DefaultConfigFactory

  class ArgmaxA extends DecisionRule {
    def computeMove(c: Config) = {
      val expectedUtilities: Map[Int, Double] = target(c)
      val maxUtility = expectedUtilities.values.max
      val currentUtility = expectedUtilities(c.centralVariableValue)
      if (maxUtility > currentUtility) {
        val maxUtilityMoves: Seq[Int] = expectedUtilities.filter(_._2 == maxUtility).map(_._1).toSeq
        val chosenMaxUtilityMove = maxUtilityMoves(Random.nextInt(maxUtilityMoves.size))
        chosenMaxUtilityMove
      } else {
        c.centralVariableValue
      }
    }
  }

  class MemoryLessTargetFunction extends TargetFunction {
    def computeExpectedUtilities(c: Config) = {
      val configurationCandidates: Set[Config] = for {
        assignment <- c.domain
      } yield c.withCentralVariableAssignment(assignment)
      val configUtilities = configurationCandidates.map(c => (c.centralVariableValue, utility(c))).toMap
      configUtilities
    }
  }

  class ParallelRandomAdjustmentSchedule(changeProbability: Double) extends AdjustmentSchedule {
    def shouldConsiderMove(currentConfiguration: Config) = {
      Random.nextDouble <= changeProbability
    }
  }

  class VertexColoringUtility extends UtilityFunction {
    def computeUtility(c: Config) = {
      val occupiedColors = c.neighborhood.values
      val numberOfConflicts = occupiedColors.filter(_ == c.centralVariableValue).size
      val numberOfNeighbors = occupiedColors.size
      val neighborsInSync = numberOfNeighbors - numberOfConflicts
      neighborsInSync
    }
  }

}
