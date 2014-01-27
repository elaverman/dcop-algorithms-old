package com.signalcollect.dcop.evaluation

import com.signalcollect.dcop.graph._
import com.signalcollect.dcop.modules.OptimizerModule
import scala.util.Random
import com.signalcollect._
import java.util.Date
import com.signalcollect.dcop._
import com.signalcollect.dcop.impl.RankedConfiguration

case class ColorPrinter[State](grid: Grid) {

  def printAnimation(outAnimation: java.io.FileWriter, outRanks: java.io.FileWriter, outIndConflicts: java.io.FileWriter)(aggregate: Map[Int, State]) = {
    val sorted = aggregate.toList.sortBy(x => x._1)
    sorted.foreach {
      case (id, (color, rank)) =>
        outAnimation.write(color.toString)
        if (outRanks != null)
          outRanks.write(rank.toString)
        val neighbors = grid.computeNeighbours(id)
        val neighborStates = neighbors.map(aggregate(_)).asInstanceOf[Iterable[(Int, Double)]]
        val conflictsForId = neighborStates.filter(x => x._1 == color)
        outIndConflicts.write(conflictsForId.size.toString)

        if ((id + 1) % grid.valuesInLine == 0) {
          outAnimation.write("\n")
          if (outRanks != null)
            outRanks.write("\n")
          outIndConflicts.write("\n")
        } else {
          outAnimation.write(",")
          if (outRanks != null)
            outRanks.write(",")
          outIndConflicts.write(",")
        }
      case (id, color) =>
        outAnimation.write(color.toString)
        //TODO: refactor this
        val neighbors = grid.computeNeighbours(id)
        val conflictsForId = neighbors.map(aggregate(_)).filter(_ == color)
        outIndConflicts.write(conflictsForId.size.toString)

        if ((id + 1) % grid.valuesInLine == 0) {
          outAnimation.write("\n")
          outIndConflicts.write("\n")
        } else {
          outAnimation.write(",")
          outIndConflicts.write(",")
        }
    }
    outAnimation.write("\n")
    if (outRanks != null)
      outRanks.write("\n")
    outIndConflicts.write("\n")
  }

  def countConflicts(aggregate: Map[Int, State]): Int = {
    val numberOfConflicts = aggregate.map {
      case (id, (color, rank)) =>
        val neighbors = grid.computeNeighbours(id)
        val neighborStates = neighbors.map(aggregate(_)).asInstanceOf[Iterable[(Int, Double)]]
        val conflictsForId = neighborStates.filter(x => x._1 == color)
        conflictsForId.size
      case (id, color) =>
        val neighbors = grid.computeNeighbours(id)
        val conflictsForId = neighbors.map(aggregate(_)).filter(_ == color)
        conflictsForId.size
    }.sum / 2
    numberOfConflicts
  }

  def printNumberOfConflicts(outConflicts: java.io.FileWriter)(aggregate: Map[Int, State]) = {
    outConflicts.write(countConflicts(aggregate) + "\n")
  }

  def shouldTerminate(outAnimation: java.io.FileWriter, outConflicts: java.io.FileWriter, outRanks: java.io.FileWriter, outIndConflicts: java.io.FileWriter)(aggregate: Map[Int, State]): Boolean = {
    print("*"+countConflicts(aggregate))
    printAnimation(outAnimation, outRanks, outIndConflicts)(aggregate)
    //    println("****")
    printNumberOfConflicts(outConflicts)(aggregate)
    //    println("____________")

    false
  }
}

class ColorPrintingGlobalTerminationCondition(
  outAnimation: java.io.FileWriter,
  outConflicts: java.io.FileWriter,
  outIndConflicts: java.io.FileWriter,
  startTime: Long,
  gridWidth: Int,
  aggregationOperation: IdStateMapAggregator[Int, Int],
  aggregationInterval: Long,
  grid: Grid) extends GlobalTerminationCondition[Map[Int, Int]](aggregationOperation, aggregationInterval, ColorPrinter(grid).shouldTerminate(outAnimation, outConflicts, null, outIndConflicts))
  with Serializable

class ColorRankPrintingGlobalTerminationCondition(
  outAnimation: java.io.FileWriter,
  outConflicts: java.io.FileWriter,
  outRanks: java.io.FileWriter,
  outIndConflicts: java.io.FileWriter,
  startTime: Long,
  gridWidth: Int,
  aggregationOperation: IdStateMapAggregator[Int, (Int, Double)],
  aggregationInterval: Long,
  grid: Grid) extends GlobalTerminationCondition[Map[Int, (Int, Double)]](aggregationOperation, aggregationInterval, ColorPrinter(grid).shouldTerminate(outAnimation, outConflicts, outRanks, outIndConflicts))
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
    val outIndConflicts = new java.io.FileWriter(s"output/indConflicts${optimizer}${executionConfig.executionMode}Run$runNumber.txt")
    var outRanks: java.io.FileWriter = null

    println(optimizer.toString)
    var finalResults = List[Map[String, String]]()

    var runResult = Map[String, String]()

    println("Executing.")

    val date: Date = new Date
    val startTime = System.nanoTime()

    val terminationCondition = if (!isOptimizerRanked)
      new ColorPrintingGlobalTerminationCondition(outAnimation, outConflicts, outIndConflicts, startTime, width, aggregationOperation = new IdStateMapAggregator[Int, Int], aggregationInterval = aggregationInterval, grid = grid)
    else {
      outRanks = new java.io.FileWriter(s"output/ranks${optimizer}${executionConfig.executionMode}Run$runNumber.txt")
      new ColorRankPrintingGlobalTerminationCondition(outAnimation, outConflicts, outRanks, outIndConflicts, startTime, width, aggregationOperation = new IdStateMapAggregator[Int, (Int, Double)], aggregationInterval = aggregationInterval, grid = grid)
    }

    val idStateMapAggregator = if (!isOptimizerRanked)
      IdStateMapAggregator[Int, Int]
    else {
      IdStateMapAggregator[Int, (Int, Double)]
    }

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
    runResult += s"conflictCount" -> ColorPrinter(grid).countConflicts(g.aggregate(idStateMapAggregator)).toString //
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

    println("\nNumber of conflicts at the end: " + ColorPrinter(grid).countConflicts(g.aggregate(idStateMapAggregator)))
    println("Shutting down.")
    g.shutdown

    outAnimation.close
    outConflicts.close
    if (outRanks != null)
      outRanks.close
    outIndConflicts.close

    runResult :: finalResults

  }
}