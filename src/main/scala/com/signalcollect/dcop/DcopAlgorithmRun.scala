package com.signalcollect.dcop

import com.signalcollect.interfaces.AggregationOperation
import com.signalcollect.dcop.graph._
import com.signalcollect.dcop.impl.RankedConfiguration
import com.signalcollect.dcop.modules.OptimizerModule
import scala.util.Random
import com.signalcollect._
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.interfaces.ModularAggregationOperation
import java.util.Date

case class Printer(grid: Grid) {

  def printAnimation(outAnimation: java.io.FileWriter)(aggregate: Map[Int, Int]) = {
    val sorted = aggregate.toList.sortBy(x => x._1)
    sorted.foreach {
      case (id, color) =>
        outAnimation.write(color.toString)
        if ((id + 1) % grid.valuesInLine == 0) {
          outAnimation.write("\n")
        } else {
          outAnimation.write(",")
        }
    }
    outAnimation.write("\n")
  }

  def countConflicts(aggregate: Map[Int, Int]): Int = {
    val numberOfConflicts = aggregate.map {
      case (id, color) =>
        val neighbors = grid.computeNeighbours(id)
        val conflictsForId = neighbors.map(aggregate(_)).filter(_ == color)
        conflictsForId.size
    }.sum / 2
    numberOfConflicts
  }

  def printNumberOfConflicts(outConflicts: java.io.FileWriter)(aggregate: Map[Int, Int]) = {
    outConflicts.write(countConflicts(aggregate) + "\n")
  }

  def shouldTerminate(outAnimation: java.io.FileWriter, outConflicts: java.io.FileWriter)(aggregate: Map[Int, Int]): Boolean = {
    print("*")
    printAnimation(outAnimation)(aggregate)
    //    println("****")
    printNumberOfConflicts(outConflicts)(aggregate)
    //    println("____________")
    false
  }
}

class ColorPrintingGlobalTerminationCondition(
  outAnimation: java.io.FileWriter,
  outConflicts: java.io.FileWriter,
  startTime: Long,
  gridWidth: Int,
  aggregationOperation: IdStateMapAggregator[Int, Int],
  aggregationInterval: Long,
  grid: Grid) extends GlobalTerminationCondition[Map[Int, Int]](aggregationOperation, aggregationInterval, Printer(grid).shouldTerminate(outAnimation, outConflicts))
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

case class GridDcopAlgorithmRun(optimizer: DcopAlgorithm[Int, Int], domain: Set[Int], width: Int, executionConfig: ExecutionConfiguration, runNumber: Int, aggregationInterval: Int, revision: String, evaluationDescription: String) {

  def roundToMillisecondFraction(nanoseconds: Long): Double = {
    ((nanoseconds / 100000.0).round) / 10.0
  }

  def runAlgorithm(): List[Map[String, String]] = {
    println("Starting.")
    val g = GraphBuilder.build

    def randomFromDomain = domain.toSeq(Random.nextInt(domain.size))
    def zeroInitialized = 0
    val debug = false

    val grid = Grid(width)

    println(optimizer)

    var isOptimizerRanked = false

    optimizer match {

      case rankedOptimizer: OptimizerModule[Int, Int] with RankedConfiguration[Int, Int] =>
        println("Ranked Optimizer")
        isOptimizerRanked = true
        for (i <- 0 until width * width)
          g.addVertex(new RankedDcopVertex(i, domain, rankedOptimizer, zeroInitialized, debug = debug))
        for (i <- 0 until width * width)
          for (n <- grid.computeNeighbours(i))
            g.addEdge(i, new RankedVertexColoringEdge(n))

      case simpleOptimizer: OptimizerModule[Int, Int] =>
        println("Simple Optimizer")
        isOptimizerRanked = true
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

    println(optimizer.toString)
    var finalResults = List[Map[String, String]]()

    var runResult = Map[String, String]()

    println("Executing.")

    val date: Date = new Date
    val startTime = System.nanoTime()

    val terminationCondition = new ColorPrintingGlobalTerminationCondition(outAnimation, outConflicts, startTime, width, aggregationOperation = new IdStateMapAggregator[Int, Int], aggregationInterval = aggregationInterval, grid = grid)
    val stats = g.execute(executionConfig.withGlobalTerminationCondition(terminationCondition))

    
 //   stats.aggregatedWorkerStatistics.numberOfOutgoingEdges
    val finishTime = System.nanoTime
    val executionTime = roundToMillisecondFraction(finishTime - startTime)
    runResult += s"evaluationDescription" -> evaluationDescription //
    runResult += s"isOptimizerRanked" -> isOptimizerRanked.toString
    runResult += s"revision" -> revision
    runResult += s"computationTimeInMilliseconds" -> executionTime.toString //
    runResult += s"executionHostname" -> java.net.InetAddress.getLocalHost.getHostName //
    runResult += s"date" -> date.toString //
    runResult += s"startTime" -> startTime.toString //
    runResult += s"endTime" -> finishTime.toString //
    runResult += s"graphStructure" -> s"Grid" //
    runResult += s"conflictCount" -> Printer(grid).countConflicts(g.aggregate(IdStateMapAggregator[Int, Int])).toString //
    runResult += s"optimizer" -> optimizer.toString //
    runResult += s"domainSize" -> domain.size.toString //
    runResult += s"graphSize" -> (width * width).toString //
    runResult += s"executionMode" -> executionConfig.executionMode.toString //
    runResult += s"signalThreshold" -> executionConfig.signalThreshold.toString // 
    runResult += s"collectThreshold" -> executionConfig.collectThreshold.toString //
    runResult += s"timeLimit" -> executionConfig.timeLimit.toString
    runResult += s"stepsLimit" -> executionConfig.stepsLimit.toString
    runResult += s"run" -> runNumber.toString
    runResult += s"aggregationInterval" -> aggregationInterval.toString

    println("\nNumber of conflicts at the end: " + Printer(grid).countConflicts(g.aggregate(IdStateMapAggregator[Int, Int])))
    println("Shutting down.")
    g.shutdown

    outAnimation.close
    outConflicts.close

    runResult :: finalResults

  }
}