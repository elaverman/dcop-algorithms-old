package com.signalcollect.dcop.evaluation

import com.signalcollect.dcop.graph._
import com.signalcollect.dcop.modules.OptimizerModule
import scala.util.Random
import com.signalcollect._
import java.util.Date
import com.signalcollect.dcop._
import com.signalcollect.dcop.impl.RankedConfiguration
import java.io.FileWriter

case class ColorPrinter[State](evaluationGraph: EvaluationGraph) {

  var iteration = 1
  var firstLocMinimum = 30000

  def printAnimation(outAnimation: FileWriter, outRanks: Option[FileWriter], outIndConflicts: FileWriter)(aggregate: Map[Int, State]) = {
    val sorted = aggregate.toList.sortBy(x => x._1)

    evaluationGraph match {
      case grid: Grid =>

        def writeGridVertexAfterEntryToFile(id: Int, file: FileWriter) {
          if ((id + 1) % grid.width == 0) {
            file.write("\n")
          } else {
            file.write(",")
          }
        }

        def writeGridVertexEntryToFile(id: Int, value: Any, file: FileWriter) {
          file.write(value.toString)
          writeGridVertexAfterEntryToFile(id, file)
        }

        def writeGridVertexEntries(id: Int, color: Int, rank: Option[Double]) {
          val neighbors = grid.computeNeighbours(id)
          val neighborStates = neighbors.map(aggregate(_)).asInstanceOf[Iterable[(Int, Double)]]
          val conflictsForId = neighborStates.filter(x => x._1 == color)
          writeGridVertexEntryToFile(id, color, outAnimation)
          writeGridVertexEntryToFile(id, conflictsForId.size, outIndConflicts)
          if (outRanks.isDefined) {
            assert(rank.isDefined, "An outRanks FileWriter can only be passed in if all vertices have ranks defined.")
            writeGridVertexEntryToFile(id, rank.get, outRanks.get)
          }
        }

        sorted.foreach {
          case (id: Int, (color: Int, rank: Double)) =>
            writeGridVertexEntries(id, color, Some(rank))
          case (id: Int, color: Int) =>
            writeGridVertexEntries(id, color, None)
        }
        writeGridVertexAfterEntryToFile(grid.width - 1, outAnimation)
        writeGridVertexAfterEntryToFile(grid.width - 1, outIndConflicts)
        if (outRanks.isDefined) {
          writeGridVertexAfterEntryToFile(grid.width - 1, outRanks.get)
        }

      case adoptGraph: AdoptGraph =>
        def writeAdoptGraphVertexEntryToFile(id: Int, value: Any, file: FileWriter) {
          file.write(s"($id,$value)")
        }

        def writeAdoptGraphVertexEntries(id: Int, color: Int, rank: Option[Double]) {
          val neighbors = adoptGraph.computeNeighbours(id)
          val neighborStates = neighbors.map(aggregate(_)).asInstanceOf[Iterable[(Int, Double)]]
          val conflictsForId = neighborStates.filter(x => x._1 == color)
          writeAdoptGraphVertexEntryToFile(id, color, outAnimation)
          writeAdoptGraphVertexEntryToFile(id, conflictsForId.size, outIndConflicts)
          if (outRanks.isDefined) {
            assert(rank.isDefined, "An outRanks FileWriter can only be passed in if all vertices have ranks defined.")
            writeAdoptGraphVertexEntryToFile(id, rank.get, outRanks.get)
          }
        }
        sorted.foreach {
          case (id: Int, (color: Int, rank: Double)) =>
            writeAdoptGraphVertexEntries(id, color, Some(rank))
          case (id: Int, color: Int) =>
            writeAdoptGraphVertexEntries(id, color, None)
        }
        outAnimation.write("\n")
        outIndConflicts.write("\n")
        if (outRanks.isDefined) {
          outRanks.get.write("\n")
        }

    }
  }

  //TODO: Attention, this computes the number of conflicts. The conflicts are only counted once for every constraint. 
  def countConflicts(aggregate: Map[Int, State]): Int = {
    val numberOfConflicts = aggregate.map {
      case (id, (color, rank)) =>
        val neighbors = evaluationGraph.computeNeighbours(id)
        val neighborStates = neighbors.map(aggregate(_)).asInstanceOf[Iterable[(Int, Double)]]
        val conflictsForId = neighborStates.filter(x => x._1 == color)
        conflictsForId.size
      case (id, color) =>
        val neighbors = evaluationGraph.computeNeighbours(id)
        val conflictsForId = neighbors.map(aggregate(_)).filter(_ == color)
        conflictsForId.size
    }.sum / 2
    numberOfConflicts
  }

  //Computes if it is in (actual) LocalMinimum (i.e. not according to beliefs - the target function) 
  def countLocMinima(aggregate: Map[Int, State]): Int = {
    val numberOfLocMinima = aggregate.map {
      case (id, (color, rank)) =>
        val neighbors = evaluationGraph.computeNeighbours(id)
        val neighborStates = neighbors.map(aggregate(_)).asInstanceOf[Iterable[(Int, Double)]]
        val conflictsForId = neighborStates.filter(x => x._1 == color)
        val minPossibleConflicts = evaluationGraph.domainForVertex(id).map(col => (neighborStates.filter(x => x._1 == col)).size).min
        //TODO: here to compute min number of conflicts given neighbor states and check if conflictsForId.size is equal to that
        if (conflictsForId.size == minPossibleConflicts) 1 else 0
      case (id, color) =>
        val neighbors = evaluationGraph.computeNeighbours(id)
        val conflictsForId = neighbors.map(aggregate(_)).filter(_ == color)
        val minPossibleConflicts = evaluationGraph.domainForVertex(id).map(col => (neighbors.filter(_ == col)).size).min
        //TODO: here to compute min number of conflicts given neighbor states and check if conflictsForId.size is equal to that
        if (conflictsForId.size == minPossibleConflicts) 1 else 0
    }.sum
    numberOfLocMinima
  }

  def printNumberOfConflicts(outConflicts: FileWriter)(aggregate: Map[Int, State]) = {
    outConflicts.write(countConflicts(aggregate) + "\n")
  }

  def printNumberOfLocalMinima(outLocMinima: FileWriter)(aggregate: Map[Int, State]) = {
    outLocMinima.write(countLocMinima(aggregate) + "\n")
  }

  def changeTimeToFirstLocOptimum(stats: RunStats)(aggregate: Map[Int, State]) = {
    if (countLocMinima(aggregate) == evaluationGraph.size) {
      stats.timeToFirstLocOptimum = stats.timeToFirstLocOptimum match {
        case None => Some(iteration)
        case Some(timeStamp) => if (timeStamp > iteration) {Some(iteration); throw new Exception("the time stamp should not be bigger")} else Some(timeStamp)
      } 
    }
  }

  def shouldTerminate(outAnimation: FileWriter, outConflicts: FileWriter, outRanks: Option[FileWriter], outIndConflicts: FileWriter, outLocMinima: FileWriter, stats: RunStats)(aggregate: Map[Int, State]): Boolean = {
    print("*" + countConflicts(aggregate))
    printAnimation(outAnimation, outRanks, outIndConflicts)(aggregate)
    printNumberOfConflicts(outConflicts)(aggregate)
    printNumberOfLocalMinima(outLocMinima)(aggregate)
    changeTimeToFirstLocOptimum(stats)(aggregate)
    //TODO: compute sum/avg (globalutility/optimalglobalutility)
    iteration += 1
    false
  }
}

class ColorPrintingGlobalTerminationCondition(
  outAnimation: FileWriter,
  outConflicts: FileWriter,
  outIndConflicts: FileWriter,
  outLocMinima: FileWriter,
  stats: RunStats,
  startTime: Long,
  //  gridWidth: Int,
  aggregationOperation: IdStateMapAggregator[Int, Int],
  aggregationInterval: Long,
  evaluationGraph: EvaluationGraph) extends GlobalTerminationCondition[Map[Int, Int]](aggregationOperation, aggregationInterval, ColorPrinter(evaluationGraph).shouldTerminate(outAnimation, outConflicts, None, outIndConflicts, outLocMinima, stats))
  with Serializable

class ColorRankPrintingGlobalTerminationCondition(
  outAnimation: FileWriter,
  outConflicts: FileWriter,
  outRanks: Option[FileWriter],
  outIndConflicts: FileWriter,
  outLocMinima: FileWriter,
  stats: RunStats,
  startTime: Long,
  //  gridWidth: Int,
  aggregationOperation: IdStateMapAggregator[Int, (Int, Double)],
  aggregationInterval: Long,
  evaluationGraph: EvaluationGraph) extends GlobalTerminationCondition[Map[Int, (Int, Double)]](aggregationOperation, aggregationInterval, ColorPrinter(evaluationGraph).shouldTerminate(outAnimation, outConflicts, outRanks, outIndConflicts, outLocMinima, stats))
  with Serializable

