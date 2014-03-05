package com.signalcollect.dcop.evaluation

import com.signalcollect.dcop.graph._
import com.signalcollect.dcop.modules.OptimizerModule
import scala.util.Random
import com.signalcollect._
import java.util.Date
import com.signalcollect.dcop._
import com.signalcollect.dcop.impl.RankedConfiguration
import java.io.FileWriter
import java.io.File
import com.signalcollect.dcop.graphstructures.Grid
import com.signalcollect.dcop.graphstructures.AdoptGraph
import com.signalcollect.dcop.graphstructures.DimacsGraph

case class DcopAlgorithmRun(optimizer: DcopAlgorithm[Int, Int], /*domain: Set[Int], */ evaluationGraphParameters: EvaluationGraphParameters, executionConfig: ExecutionConfiguration, runNumber: Int, aggregationInterval: Int, revision: String, evaluationDescription: String) {

  def roundToMillisecondFraction(nanoseconds: Long): Double = {
    ((nanoseconds / 100000.0).round) / 10.0
  }

  def runAlgorithm(): List[Map[String, String]] = {
    println("Starting.")

    val evaluationGraph = evaluationGraphParameters match {
      case gridParameters: GridParameters => Grid(optimizer, gridParameters.domain, gridParameters.initialValue, gridParameters.debug, gridParameters.width)
      case adoptGraphParameters: AdoptGraphParameters => AdoptGraph(optimizer, adoptGraphParameters.adoptFileName, adoptGraphParameters.initialValue, adoptGraphParameters.debug)
      case dimacsGraphParameters: DimacsGraphParameters => DimacsGraph(optimizer, dimacsGraphParameters.domain, dimacsGraphParameters.dimacsFileName, dimacsGraphParameters.initialValue, dimacsGraphParameters.debug)
    }

    println(optimizer)

    var computeRanks = false

    optimizer match {

      case rankedOptimizer: OptimizerModule[Int, Int] with RankedConfiguration[Int, Int] =>
        println("Ranked Optimizer")
        computeRanks = true

      case simpleOptimizer: OptimizerModule[Int, Int] =>
        println("Simple Optimizer")
        computeRanks = true
    }
    println("Preparing Execution configuration.")
    println(executionConfig.executionMode)

    val graphDirectoryFolder = new File("output/" + evaluationGraph.toString())
    if (!graphDirectoryFolder.exists)
      graphDirectoryFolder.mkdir
    val outAnimation = new FileWriter(s"output/${evaluationGraph}/animation${optimizer}${executionConfig.executionMode}${executionConfig.stepsLimit}Run$runNumber.txt")
    val outConflicts = new FileWriter(s"output/${evaluationGraph}/conflicts${optimizer}${executionConfig.executionMode}${executionConfig.stepsLimit}Run$runNumber.txt")
    val outIndConflicts = new FileWriter(s"output/${evaluationGraph}/indConflicts${optimizer}${executionConfig.executionMode}${executionConfig.stepsLimit}Run$runNumber.txt")
    val outLocMinima = new FileWriter(s"output/${evaluationGraph}/locMinima${optimizer}${executionConfig.executionMode}${executionConfig.stepsLimit}Run$runNumber.txt")
    var outRanks: FileWriter = null

    println(optimizer.toString)
    var finalResults = List[Map[String, String]]()

    var runResult = Map[String, String]()

    println("Executing.")

    val date: Date = new Date
    val startTime = System.nanoTime()
    var extraStats = RunStats(None, evaluationGraph.maxUtility, None)

    val terminationCondition = if (!computeRanks)
      new ColorPrintingGlobalTerminationCondition(outAnimation, outConflicts, outIndConflicts, outLocMinima, extraStats, startTime, aggregationOperation = new IdStateMapAggregator[Int, Int], aggregationInterval = aggregationInterval, evaluationGraph = evaluationGraph)
    else {
      outRanks = new java.io.FileWriter(s"output/${evaluationGraph}/ranks${optimizer}${executionConfig.executionMode}${executionConfig.stepsLimit}Run$runNumber.txt")
      new ColorRankPrintingGlobalTerminationCondition(outAnimation, outConflicts, Some(outRanks), outIndConflicts, outLocMinima, extraStats, startTime, aggregationOperation = new IdStateMapAggregator[Int, (Int, Double)], aggregationInterval = aggregationInterval, evaluationGraph = evaluationGraph)
    }

    val idStateMapAggregator = if (!computeRanks)
      IdStateMapAggregator[Int, Int]
    else {
      IdStateMapAggregator[Int, (Int, Double)]
    }

    val stats = evaluationGraph.graph.execute(executionConfig.withGlobalTerminationCondition(terminationCondition))

    //   stats.aggregatedWorkerStatistics.numberOfOutgoingEdges
    val finishTime = System.nanoTime
    val executionTime = roundToMillisecondFraction(finishTime - startTime)

    val conflictCount = ColorPrinter(evaluationGraph).countConflicts(evaluationGraph.graph.aggregate(idStateMapAggregator))
    
    val utility = (evaluationGraph.maxUtility - conflictCount * 2).toDouble
    val domainSize = evaluationGraph match {
      case grid: Grid => grid.domain.size
      case dimacsGraph: DimacsGraph => dimacsGraph.domain.size
      case other => -1
    }
    
    val avgGlobalUtilityRatio = extraStats.avgGlobalVsOpt.getOrElse(-1)
    val endUtilityRatio = (evaluationGraph.maxUtility - conflictCount * 2).toDouble / evaluationGraph.maxUtility
    val isOptimal = if (conflictCount == 0) 1 else 0
    val timeToFirstLocOptimum = extraStats.timeToFirstLocOptimum.getOrElse(-1)
    val messagesPerVertexPerStep = stats.aggregatedWorkerStatistics.signalMessagesReceived.toDouble / (evaluationGraph.size.toDouble * executionConfig.stepsLimit.getOrElse(1.toLong))
    runResult += s"evaluationDescription" -> evaluationDescription //
    runResult += s"optimizer" -> optimizer.toString //
    runResult += s"utility" -> utility.toString
    runResult += s"domainSize" -> domainSize.toString
    runResult += s"graphSize" -> evaluationGraph.size.toString //
    runResult += s"executionMode" -> executionConfig.executionMode.toString //
    runResult += s"conflictCount" -> conflictCount.toString //
    runResult += s"avgGlobalUtilityRatio" -> avgGlobalUtilityRatio.toString // Measure (1)
    runResult += s"endUtilityRatio" -> endUtilityRatio.toString // Measure (2)
    runResult += s"isOptimal" -> isOptimal.toString // Measure (3)
    runResult += s"timeToFirstLocOptimum" -> timeToFirstLocOptimum.toString // Measure (4)
    runResult += s"messagesPerVertexPerStep" -> messagesPerVertexPerStep.toString // Measure (5)
    runResult += s"isOptimizerRanked" -> computeRanks.toString
    runResult += s"revision" -> revision
    runResult += s"aggregationInterval" -> aggregationInterval.toString
    runResult += s"run" -> runNumber.toString
    runResult += s"stepsLimit" -> executionConfig.stepsLimit.toString
    runResult += s"timeLimit" -> executionConfig.timeLimit.toString
    runResult += s"graphStructure" -> evaluationGraph.toString //

    runResult += s"computationTimeInMilliseconds" -> executionTime.toString //
    runResult += s"date" -> date.toString //
    runResult += s"executionHostname" -> java.net.InetAddress.getLocalHost.getHostName //

    runResult += s"signalThreshold" -> executionConfig.signalThreshold.toString // 
    runResult += s"collectThreshold" -> executionConfig.collectThreshold.toString //

    // runResult += s"startTime" -> startTime.toString //
    // runResult += s"endTime" -> finishTime.toString //


    println("\nNumber of conflicts at the end: " + ColorPrinter(evaluationGraph).countConflicts(evaluationGraph.graph.aggregate(idStateMapAggregator)))
    println("Shutting down.")
    evaluationGraph.graph.shutdown

    outAnimation.close
    outConflicts.close
    outLocMinima.close
    if (outRanks != null)
      outRanks.close
    outIndConflicts.close

    runResult :: finalResults

  }
}