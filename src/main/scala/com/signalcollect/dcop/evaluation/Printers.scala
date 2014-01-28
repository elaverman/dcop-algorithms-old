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

  def printNumberOfConflicts(outConflicts: FileWriter)(aggregate: Map[Int, State]) = {
    outConflicts.write(countConflicts(aggregate) + "\n")
  }

  def shouldTerminate(outAnimation: FileWriter, outConflicts: FileWriter, outRanks: Option[FileWriter], outIndConflicts: FileWriter)(aggregate: Map[Int, State]): Boolean = {
    print("*" + countConflicts(aggregate))
    printAnimation(outAnimation, outRanks, outIndConflicts)(aggregate)
    //    println("****")
    printNumberOfConflicts(outConflicts)(aggregate)
    //    println("____________")
    false
  }
}

class ColorPrintingGlobalTerminationCondition(
  outAnimation: FileWriter,
  outConflicts: FileWriter,
  outIndConflicts: FileWriter,
  startTime: Long,
  //  gridWidth: Int,
  aggregationOperation: IdStateMapAggregator[Int, Int],
  aggregationInterval: Long,
  evaluationGraph: EvaluationGraph) extends GlobalTerminationCondition[Map[Int, Int]](aggregationOperation, aggregationInterval, ColorPrinter(evaluationGraph).shouldTerminate(outAnimation, outConflicts, None, outIndConflicts))
  with Serializable

class ColorRankPrintingGlobalTerminationCondition(
  outAnimation: FileWriter,
  outConflicts: FileWriter,
  outRanks: Option[FileWriter],
  outIndConflicts: FileWriter,
  startTime: Long,
  //  gridWidth: Int,
  aggregationOperation: IdStateMapAggregator[Int, (Int, Double)],
  aggregationInterval: Long,
  evaluationGraph: EvaluationGraph) extends GlobalTerminationCondition[Map[Int, (Int, Double)]](aggregationOperation, aggregationInterval, ColorPrinter(evaluationGraph).shouldTerminate(outAnimation, outConflicts, outRanks, outIndConflicts))
  with Serializable

