package com.signalcollect.dcop

import com.signalcollect.interfaces.AggregationOperation
import com.signalcollect.dcop.graph._
import com.signalcollect.dcop.impl.RankedConfiguration
import com.signalcollect.dcop.modules.OptimizerModule
import scala.util.Random
import com.signalcollect._
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.interfaces.ModularAggregationOperation

case class ColorRankPrinter(valuesInLine: Int) {
  def shouldTerminate(f: java.io.FileWriter)(aggregate: Map[Int, (Int, Double)]): Boolean = {
    println("Currently in printer object")
    val sorted = aggregate.toList.sortBy(x => x._1)
    sorted.foreach {
      case (id, (color, rank)) =>
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

class ColorRankPrintingGlobalTerminationCondition(
  f: java.io.FileWriter,
  /*g: java.io.FileWriter,*/
  startTime: Long,
  gridWidth: Int,
  aggregationOperation: IdStateMapAggregator[Int, (Int, Double)],
  aggregationInterval: Long) extends GlobalTerminationCondition[Map[Int, (Int, Double)]](aggregationOperation, aggregationInterval, ColorRankPrinter(gridWidth).shouldTerminate(f))
  with Serializable

//class GlobalUtility extends AggregationOperation[(Int, Double)] {
//  val neutralElement = (0, 0.0)
//  def extract(v: Vertex[_, _]): (Int, Double) = v match {
//    case vertex: DSANVertex => (vertex.constraints.size, vertex.utility)
//    case vertex: JSFPIVertex => (vertex.constraints.size, vertex.utility)
//    case other => neutralElement
//  }
//  def reduce(elements: Stream[(Int, Double)]) = elements.foldLeft(neutralElement)(aggregate)
//  def aggregate(a: (Int, Double), b: (Int, Double)): (Int, Double) = (a._1 + b._1, a._2 + b._2)
//}

object RankedDcopAlgorithmRun extends App {

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
//  val optimizer: OptimizerModule[Int, Int] with RankedConfiguration[Int, Int] = RankedDsaAVertexColoring(changeProbability = 1.0)
  val optimizer: OptimizerModule[Int, Int] with RankedConfiguration[Int, Int] = RankedDsaBVertexColoring(changeProbability = 1.0)
  val domain = (0 to 3).toSet
  val width = 100
  def randomFromDomain = domain.toSeq(Random.nextInt(domain.size))
  val debug = false

  for (i <- 0 until width * width)
    g.addVertex(new RankedDcopVertex(i, domain, optimizer, randomFromDomain, debug = debug))

  for (i <- 0 until width * width)
    for (n <- neighbours(i, width))
      g.addEdge(i, new RankedVertexColoringEdge(n))

  println("Preparing Execution configuration.")

  val out = new java.io.FileWriter("animation.txt")
  val outTime = new java.io.FileWriter("resultsTime.txt")
  var startTime = System.nanoTime()
  val terminationCondition = new ColorRankPrintingGlobalTerminationCondition(out, /* outTime,*/ startTime, width, aggregationOperation = new IdStateMapAggregator[Int, (Int, Double)], aggregationInterval = 100L)

  val executionConfigSync = ExecutionConfiguration(ExecutionMode.PureAsynchronous).withSignalThreshold(0.01).withGlobalTerminationCondition(terminationCondition).withTimeLimit(100000L) //(420000)

  println("Executing.")
  g.execute(executionConfigSync)
  println("Shutting down.")
  g.shutdown

}