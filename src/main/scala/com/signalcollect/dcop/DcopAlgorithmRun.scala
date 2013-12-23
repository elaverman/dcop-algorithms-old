package com.signalcollect.dcop

import com.signalcollect.interfaces.AggregationOperation
import com.signalcollect.dcop.graph._
import com.signalcollect.dcop.impl.RankedConfiguration
import com.signalcollect.dcop.modules.OptimizerModule
import scala.util.Random
import com.signalcollect._
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.interfaces.ModularAggregationOperation

case class ColorPrinter(grid: Grid) {
  def shouldTerminate(f: java.io.FileWriter, g: java.io.FileWriter)(aggregate: Map[Int, Int]): Boolean = {
    println("Currently in printer object")
    val sorted = aggregate.toList.sortBy(x => x._1)
    sorted.foreach {
      case (id, color) =>
        f.write(color.toString)
        //print(color.toString)
        if ((id + 1) % grid.valuesInLine == 0) {
          f.write("\n")
          //println
        } else {
          f.write(",")
          //print(",")
        }
    }
    f.write("\n")
    println("****")
    val numberOfConflicts = aggregate.map {
      case (id, color) =>
        val neighbors = grid.computeNeighbours(id)
        val conflictsForId = neighbors.map(aggregate(_)).filter(_ == color)
        conflictsForId.size
    }.sum / 2
    g.write(numberOfConflicts + "\n")
    println("____________")
    false
  }
}

class ColorPrintingGlobalTerminationCondition(
  f: java.io.FileWriter,
  g: java.io.FileWriter,
  startTime: Long,
  gridWidth: Int,
  aggregationOperation: IdStateMapAggregator[Int, Int],
  aggregationInterval: Long,
  grid: Grid) extends GlobalTerminationCondition[Map[Int, Int]](aggregationOperation, aggregationInterval, ColorPrinter(grid).shouldTerminate(f, g))
  with Serializable

case class Grid(valuesInLine: Int) {
  // Returns all the neighboring cells of the cell with the given row/column
  def potentialNeighbours(column: Int, row: Int): List[(Int, Int)] = {
    List(
      (column - 1, row - 1), (column, row - 1), (column + 1, row - 1),
      (column - 1, row), (column + 1, row),
      (column - 1, row + 1), (column, row + 1), (column + 1, row + 1))
  }

  // Tests if a cell is within the grid boundaries
  def inGrid(column: Int, row: Int): Boolean = {
    column >= 0 && row >= 0 && column < valuesInLine && row < valuesInLine
  }

  def computeNeighbours(id: Int): Iterable[Int] = {
    val column: Int = id % valuesInLine
    val row: Int = id / valuesInLine
    potentialNeighbours(column, row).filter(coordinate => inGrid(coordinate._1, coordinate._2)) map
      (coordinate => (coordinate._2 * valuesInLine + coordinate._1))
  }
}

case class GridDcopAlgorithmRun(optimizer: DcopAlgorithm[Int, Int], domain: Set[Int], width: Int, runNumber: Int) {

  println("Starting.")
  val g = GraphBuilder.build

  def randomFromDomain = domain.toSeq(Random.nextInt(domain.size))
  val debug = false

  val grid = Grid(width)

  optimizer match {
    
    case rankedOptimizer: OptimizerModule[Int, Int] with RankedConfiguration[Int, Int] =>
      println("Ranked Optimizer")
      for (i <- 0 until width * width)
        g.addVertex(new RankedDcopVertex(i, domain, rankedOptimizer, randomFromDomain, debug = debug))
      for (i <- 0 until width * width)
        for (n <- grid.computeNeighbours(i))
          g.addEdge(i, new RankedVertexColoringEdge(n))
          
    case simpleOptimizer: OptimizerModule[Int, Int] =>
      println("Simple Optimizer")
      for (i <- 0 until width * width)
        g.addVertex(new SimpleDcopVertex(i, domain, simpleOptimizer, randomFromDomain, debug = debug))
      for (i <- 0 until width * width)
        for (n <- grid.computeNeighbours(i))
          g.addEdge(i, new StateForwarderEdge(n))
  }
  println("Preparing Execution configuration.")

  //TODO: modify output file names
  val out = new java.io.FileWriter(s"animation$runNumber.txt")
  val outConflicts = new java.io.FileWriter(s"conflicts$runNumber.txt")
  var startTime = System.nanoTime()
  val terminationCondition = new ColorPrintingGlobalTerminationCondition(out, outConflicts, startTime, width, aggregationOperation = new IdStateMapAggregator[Int, Int], aggregationInterval = 100L, grid = grid)

  val executionConfigSync = ExecutionConfiguration(ExecutionMode.PureAsynchronous).withSignalThreshold(0.01).withGlobalTerminationCondition(terminationCondition).withTimeLimit(100000L) //(420000)

  println("Executing.")
  g.execute(executionConfigSync)
  println("Shutting down.")
  g.shutdown

  out.close
  outConflicts.close

}