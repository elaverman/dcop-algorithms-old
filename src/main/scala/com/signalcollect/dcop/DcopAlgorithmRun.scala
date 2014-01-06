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

case class GridDcopAlgorithmRun(optimizer: DcopAlgorithm[Int, Int], domain: Set[Int], width: Int, executionConfig: ExecutionConfiguration, runNumber: Int, aggregationInterval: Int) {

  println("Starting.")
  val g = GraphBuilder.build

  def randomFromDomain = domain.toSeq(Random.nextInt(domain.size))
  def zeroInitialized = 0
  val debug = false

  val grid = Grid(width)

  println(optimizer)
  optimizer match {
    
    case rankedOptimizer: OptimizerModule[Int, Int] with RankedConfiguration[Int, Int] =>
      println("Ranked Optimizer")
      for (i <- 0 until width * width)
        g.addVertex(new RankedDcopVertex(i, domain, rankedOptimizer, zeroInitialized, debug = debug))
      for (i <- 0 until width * width)
        for (n <- grid.computeNeighbours(i))
          g.addEdge(i, new RankedVertexColoringEdge(n))
          
    case simpleOptimizer: OptimizerModule[Int, Int] =>
      println("Simple Optimizer")
      for (i <- 0 until width * width)
        g.addVertex(new SimpleDcopVertex(i, domain, simpleOptimizer, zeroInitialized, debug = debug))
      for (i <- 0 until width * width)
        for (n <- grid.computeNeighbours(i))
          g.addEdge(i, new StateForwarderEdge(n))
  }
  println("Preparing Execution configuration.")
  println(executionConfig.executionMode)
  
  val outAnimation = new java.io.FileWriter(s"output/animation${optimizer}${executionConfig.executionMode}Run$runNumber.txt")
  val outConflicts = new java.io.FileWriter(s"output/conflicts${optimizer}${executionConfig.executionMode}Run$runNumber.txt")
  var startTime = System.nanoTime()
  val terminationCondition = new ColorPrintingGlobalTerminationCondition(outAnimation, outConflicts, startTime, width, aggregationOperation = new IdStateMapAggregator[Int, Int], aggregationInterval = aggregationInterval, grid = grid)


  println("Executing.")
  g.execute(executionConfig.withGlobalTerminationCondition(terminationCondition))
  println("Shutting down.")
  g.shutdown

  outAnimation.close
  outConflicts.close

}