package com.signalcollect.dcop

import com.signalcollect.interfaces.AggregationOperation
import com.signalcollect.dcop.graph._
import com.signalcollect.dcop.impl.RankedConfiguration
import com.signalcollect.dcop.modules.OptimizerModule
import scala.util.Random
import com.signalcollect._
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.interfaces.ModularAggregationOperation

case class ColorPrinter(valuesInLine: Int) {
  def shouldTerminate(f: java.io.FileWriter)(aggregate: Map[Int, Int]): Boolean = {
    println("Currently in printer object")
    val sorted = aggregate.toList.sortBy(x => x._1)
    sorted.foreach {
      case (id, color) =>
        f.write(color.toString)
        //print(color.toString)
        if ((id + 1) % valuesInLine == 0) {
          f.write("\n")
          //println
        } else {
          f.write(",")
          //print(",")
        }
    }
    f.write("\n")
    println("****")
    false
  }
}

class ColorPrintingGlobalTerminationCondition(
  f: java.io.FileWriter,
  /*g: java.io.FileWriter,*/
  startTime: Long,
  gridWidth: Int,
  aggregationOperation: IdStateMapAggregator[Int, Int],
  aggregationInterval: Long) extends GlobalTerminationCondition[Map[Int, Int]](aggregationOperation, aggregationInterval, ColorPrinter(gridWidth).shouldTerminate(f))
  with Serializable

object DcopAlgorithmRun extends App {

  // Returns all the neighboring cells of the cell with the given row/column
  def potentialNeighbours(column: Int, row: Int): List[(Int, Int)] = {
    List(
      (column - 1, row - 1), (column, row - 1), (column + 1, row - 1),
      (column - 1, row), (column + 1, row),
      (column - 1, row + 1), (column, row + 1), (column + 1, row + 1))
  }

  // Tests if a cell is within the grid boundaries
  def inGrid(column: Int, row: Int, width: Int): Boolean = {
    column >= 0 && row >= 0 && column < width && row < width
  }

  def neighbours(id: Int, width: Int): Iterable[Int] = {

    val column: Int = id % width
    val row: Int = id / width

    potentialNeighbours(column, row).filter(coordinate => inGrid(coordinate._1, coordinate._2, width)) map
      (coordinate => (coordinate._2 * width + coordinate._1))
  }

  println("Starting.")
  val g = GraphBuilder.build
  //  val optimizer = DsaAVertexColoring(changeProbability = 1.0)
//  val optimizer = DsaBVertexColoring(changeProbability = 1.0)
    val optimizer = ConflictDsaBVertexColoring(changeProbability = 1.0)
  val domain = (0 to 3).toSet
  val width = 100
  def randomFromDomain = domain.toSeq(Random.nextInt(domain.size))
  val debug = false

  for (i <- 0 until width * width)
    g.addVertex(new SimpleDcopVertex(i, domain, optimizer, randomFromDomain, debug = debug))

  for (i <- 0 until width * width)
    for (n <- neighbours(i, width))
      g.addEdge(i, new StateForwarderEdge(n))

  println("Preparing Execution configuration.")

  val out = new java.io.FileWriter("animation.txt")
  val outTime = new java.io.FileWriter("resultsTime.txt")
  var startTime = System.nanoTime()
  val terminationCondition = new ColorPrintingGlobalTerminationCondition(out, /* outTime,*/ startTime, width, aggregationOperation = new IdStateMapAggregator[Int, Int], aggregationInterval = 100L)

  val executionConfigSync = ExecutionConfiguration(ExecutionMode.PureAsynchronous).withSignalThreshold(0.01).withGlobalTerminationCondition(terminationCondition).withTimeLimit(100000L) //(420000)

  println("Executing.")
  g.execute(executionConfigSync)
  println("Shutting down.")
  g.shutdown

}