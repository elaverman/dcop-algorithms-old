//package com.signalcollect.dcop.evaluation
//
//import com.signalcollect.dcop.graph._
//import com.signalcollect.dcop.modules._
//import scala.util.Random
//import com.signalcollect._
//import java.util.Date
//import com.signalcollect.dcop._
//import java.io.FileWriter
//import java.io.File
//import com.signalcollect.dcop.graphstructures.Grid
//import com.signalcollect.dcop.graphstructures._
//import com.signalcollect.dcop.impl._
//
//case class DcopAlgorithmRun[AgentId, Action, UtilityType](optimizer: Optimizer[AgentId, Action, Configuration[AgentId, Action], Double], evaluationGraph: EvaluationGraph[AgentId, Action], /*evaluationGraphParameters: EvaluationGraphParameters[Action],*/ executionConfig: ExecutionConfiguration, runNumber: Int, aggregationInterval: Int, revision: String, evaluationDescription: String) {
//
//  def roundToMillisecondFraction(nanoseconds: Long): Double = {
//    ((nanoseconds / 100000.0).round) / 10.0
//  }
//
//  def runAlgorithm(): List[Map[String, String]] = {
//    println("Starting.")
//
//    println(optimizer)
//
//    var computeRanks = false
//
//    optimizer match {
//
//      case rankedOptimizer: RankedOptimizer[AgentId, Action] =>
//        println("Ranked Optimizer")
//        computeRanks = true
//
//      case simpleOptimizer: SimpleOptimizer[AgentId, Action] =>
//        println("Simple Optimizer")
//        computeRanks = true
//    }
//    println("Preparing Execution configuration.")
//    println(executionConfig.executionMode)
//
//    //TODO: Replace ${evaluationGraph.domainForVertex(1)} from the file names with something better.
//    val graphDirectoryFolder = new File("output/" + evaluationGraph.toString())
//    if (!graphDirectoryFolder.exists)
//      graphDirectoryFolder.mkdir
//    //    val outAnimation = new FileWriter(s"output/${evaluationGraph}/animation${optimizer}${executionConfig.executionMode}${executionConfig.stepsLimit}${evaluationGraph.domainForVertex(1).size}Run$runNumber.txt")
//    val outConflicts = new FileWriter(s"output/${evaluationGraph}/conflicts${optimizer}${executionConfig.executionMode}${executionConfig.stepsLimit}Run$runNumber.txt")
//    //    val outIndConflicts = new FileWriter(s"output/${evaluationGraph}/indConflicts${optimizer}${executionConfig.executionMode}${executionConfig.stepsLimit}${evaluationGraph.domainForVertex(1).size}Run$runNumber.txt")
//    val outLocMinima = new FileWriter(s"output/${evaluationGraph}/locMinima${optimizer}${executionConfig.executionMode}${executionConfig.stepsLimit}Run$runNumber.txt")
//    //    var outRanks: FileWriter = null
//
//    println(optimizer.toString)
//    var finalResults = List[Map[String, String]]()
//
//    var runResult = Map[String, String]()
//
//    val date: Date = new Date
//    val startTime = System.nanoTime()
//    var extraStats = RunStats(None, evaluationGraph.maxUtility, None)
//    val terminationCondition = new ColorPrintingGlobalTerminationCondition(outConflicts, outLocMinima, extraStats, startTime, aggregationOperationParam = new IdStateMapAggregator[AgentId, Action], aggregationIntervalParam = aggregationInterval, evaluationGraph = evaluationGraph)
//    //val terminationCondition = if (!computeRanks)
//    //      new ColorPrintingGlobalTerminationCondition(outAnimation, outConflicts, outIndConflicts, outLocMinima, extraStats, startTime, aggregationOperationParam = new IdStateMapAggregator[Int, Int], aggregationIntervalParam = aggregationInterval, evaluationGraph = evaluationGraph)
//    //    else {
//    //      outRanks = new java.io.FileWriter(s"output/${evaluationGraph}/ranks${optimizer}${executionConfig.executionMode}${executionConfig.stepsLimit}${evaluationGraph.domainForVertex(1).size}Run$runNumber.txt")
//    //      new ColorRankPrintingGlobalTerminationCondition(outAnimation, outConflicts, Some(outRanks), outIndConflicts, outLocMinima, extraStats, startTime, aggregationOperationParam = new IdStateMapAggregator[Int, (Int, Double)], aggregationIntervalParam = aggregationInterval, evaluationGraph = evaluationGraph)
//    //    }
//
//    val idStateMapAggregator = if (!computeRanks)
//      IdStateMapAggregator[AgentId, Int]
//    else {
//      IdStateMapAggregator[AgentId, (Int, Double)]
//    }
//
//    val initialAggregate = evaluationGraph.graph.aggregate(idStateMapAggregator)
//    println(evaluationGraph)
//    println("*Initial aggregate " + initialAggregate.toMap.mkString(" "))
//
//    ColorPrinter(evaluationGraph).shouldTerminate(outConflicts, outLocMinima, extraStats, evaluationGraph.maxUtility)(initialAggregate)
//    val stats = evaluationGraph.graph.execute(executionConfig.withGlobalTerminationCondition(terminationCondition))
//
//    println("*Executing.")
//    //   stats.aggregatedWorkerStatistics.numberOfOutgoingEdges
//    val finishTime = System.nanoTime
//    val executionTime = roundToMillisecondFraction(finishTime - startTime)
//
//    val conflictCount = ColorPrinter(evaluationGraph).countConflicts(evaluationGraph.graph.aggregate(idStateMapAggregator))
//
//    val utility = (evaluationGraph.maxUtility - conflictCount * 2).toDouble
//    val domainSize = evaluationGraph match {
//      case grid: Grid[_] => grid.domain.size
//      case dimacsGraph: DimacsGraph[_] => dimacsGraph.domain.size
//      case other => -1
//    }
//
//    val avgGlobalUtilityRatio = extraStats.avgGlobalVsOpt.getOrElse(-1)
//    val endUtilityRatio = (evaluationGraph.maxUtility - conflictCount * 2).toDouble / evaluationGraph.maxUtility
//    val isOptimal = if (conflictCount == 0) 1 else 0
//    val timeToFirstLocOptimum = extraStats.timeToFirstLocOptimum.getOrElse(-1)
//    val messagesPerVertexPerStep = stats.aggregatedWorkerStatistics.signalMessagesReceived.toDouble / (evaluationGraph.size.toDouble * executionConfig.stepsLimit.getOrElse(1.toLong))
//    runResult += s"evaluationDescription" -> evaluationDescription //
//    runResult += s"optimizer" -> optimizer.toString //
//    runResult += s"utility" -> utility.toString
//    runResult += s"domainSize" -> domainSize.toString
//    runResult += s"graphSize" -> evaluationGraph.size.toString //
//    runResult += s"executionMode" -> executionConfig.executionMode.toString //
//    runResult += s"conflictCount" -> conflictCount.toString //
//    runResult += s"avgGlobalUtilityRatio" -> avgGlobalUtilityRatio.toString // Measure (1)
//    runResult += s"endUtilityRatio" -> endUtilityRatio.toString // Measure (2)
//    runResult += s"isOptimal" -> isOptimal.toString // Measure (3)
//    runResult += s"timeToFirstLocOptimum" -> timeToFirstLocOptimum.toString // Measure (4)
//    runResult += s"messagesPerVertexPerStep" -> messagesPerVertexPerStep.toString // Measure (5)
//    runResult += s"isOptimizerRanked" -> computeRanks.toString
//    runResult += s"revision" -> revision
//    runResult += s"aggregationInterval" -> aggregationInterval.toString
//    runResult += s"run" -> runNumber.toString
//    runResult += s"stepsLimit" -> executionConfig.stepsLimit.toString
//    runResult += s"timeLimit" -> executionConfig.timeLimit.toString
//    runResult += s"graphStructure" -> evaluationGraph.toString //
//
//    runResult += s"computationTimeInMilliseconds" -> executionTime.toString //
//    runResult += s"date" -> date.toString //
//    runResult += s"executionHostname" -> java.net.InetAddress.getLocalHost.getHostName //
//
//    runResult += s"signalThreshold" -> executionConfig.signalThreshold.toString // 
//    runResult += s"collectThreshold" -> executionConfig.collectThreshold.toString //
//
//    println("\nNumber of conflicts at the end: " + ColorPrinter(evaluationGraph).countConflicts(evaluationGraph.graph.aggregate(idStateMapAggregator)))
//    println("Shutting down.")
//    evaluationGraph.graph.shutdown
//
//    //    outAnimation.close
//    outConflicts.close
//    outLocMinima.close
//    //    if (outRanks != null)
//    //      outRanks.close
//    //    outIndConflicts.close
//
//    runResult :: finalResults
//
//  }
//}
//
////TODO Ugly. Rewrite
//case class DcopMixedAlgorithmRun[AgentId, Action, UtilityType](optimizer1: Optimizer[AgentId, Action, Configuration[AgentId, Action], UtilityType], optimizer2: Optimizer[AgentId, Action, Configuration[AgentId, Action], UtilityType], proportion: Double, /*domain: Set[Int], */ evaluationGraph: EvaluationGraph[AgentId, Action], executionConfig: ExecutionConfiguration, runNumber: Int, aggregationInterval: Int, revision: String, evaluationDescription: String) {
//
//  def roundToMillisecondFraction(nanoseconds: Long): Double = {
//    ((nanoseconds / 100000.0).round) / 10.0
//  }
//
//  def runAlgorithm(): List[Map[String, String]] = {
//    println("*Starting.")
//
////    val evaluationGraph = evaluationGraphParameters match {
////      case gridParameters: GridParameters => throw new Error("MIXED Dimacs graph still unsupported.")
////      case adoptGraphParameters: AdoptGraphParameters => MixedAdoptGraph(optimizer1, optimizer2, proportion, adoptGraphParameters.adoptFileName, adoptGraphParameters.initialValue, adoptGraphParameters.debug)
////      case dimacsGraphParameters: DimacsGraphParameters => throw new Error("MIXED Dimacs graph still unsupported.")
////    }
//
//    println(optimizer1 + " " + optimizer2 + " " + proportion)
//
//    var computeRanks = false
//
//    //TODO Check if the matching is correctly done
//
//    optimizer1 match {
//      case rankedOptimizer: RankedOptimizer[AgentId, Action] =>
//        println("Ranked Optimizers")
//        computeRanks = true
//      case simpleOptimizer: SimpleOptimizer[AgentId, Action] =>
//        println("Simple Optimizers")
//        computeRanks = true //TODO Verify why this was set to true instead of false
//    }
//
//    //println("Preparing Execution configuration.")
//    //println(executionConfig.executionMode)
//
//    //TODO: Replace ${evaluationGraph.domainForVertex(1)} from the file names with something better.
//    val graphDirectoryFolder = new File("output/" + evaluationGraph.toString())
//    if (!graphDirectoryFolder.exists)
//      graphDirectoryFolder.mkdir
//    //    val outAnimation = new FileWriter(s"output/${evaluationGraph}/animation${optimizer1}${optimizer2}${proportion}${executionConfig.executionMode}${executionConfig.stepsLimit}Run$runNumber.txt")
//    val outConflicts = new FileWriter(s"output/${evaluationGraph}/conflicts${optimizer1}${optimizer2}${proportion}${executionConfig.executionMode}${executionConfig.stepsLimit}Run$runNumber.txt")
//    //    val outIndConflicts = new FileWriter(s"output/${evaluationGraph}/indConflicts${optimizer1}${optimizer2}${proportion}${executionConfig.executionMode}${executionConfig.stepsLimit}Run$runNumber.txt")
//    val outLocMinima = new FileWriter(s"output/${evaluationGraph}/locMinima${optimizer1}${optimizer2}${proportion}${executionConfig.executionMode}${executionConfig.stepsLimit}Run$runNumber.txt")
//    //    var outRanks: FileWriter = null
//
//    //println(optimizer.toString)
//    var finalResults = List[Map[String, String]]()
//
//    var runResult = Map[String, String]()
//
//    val date: Date = new Date
//    val startTime = System.nanoTime()
//    var extraStats = RunStats(None, evaluationGraph.maxUtility, None)
//
//    val terminationCondition = new ColorPrintingGlobalTerminationCondition(outConflicts, outLocMinima, extraStats, startTime, aggregationOperationParam = new IdStateMapAggregator[AgentId, Action], aggregationIntervalParam = aggregationInterval, evaluationGraph = evaluationGraph)
//    //    = if (!computeRanks)
//    //      //new ColorPrintingGlobalTerminationCondition(outAnimation, outConflicts, outIndConflicts, outLocMinima, extraStats, startTime, aggregationOperationParam = new IdStateMapAggregator[Int, Int], aggregationIntervalParam = aggregationInterval, evaluationGraph = evaluationGraph)
//    //      new ColorPrintingGlobalTerminationCondition(outConflicts, outLocMinima, extraStats, startTime, aggregationOperationParam = new IdStateMapAggregator[Int, Int], aggregationIntervalParam = aggregationInterval, evaluationGraph = evaluationGraph)
//    //    else {
//    //      outRanks = new java.io.FileWriter(s"output/${evaluationGraph}/ranks${optimizer1}${optimizer2}${proportion}${executionConfig.executionMode}${executionConfig.stepsLimit}${evaluationGraph.domainForVertex(1).size}Run$runNumber.txt")
//    //      //      new ColorRankPrintingGlobalTerminationCondition(outAnimation, outConflicts, Some(outRanks), outIndConflicts, outLocMinima, extraStats, startTime, aggregationOperationParam = new IdStateMapAggregator[Int, (Int, Double)], aggregationIntervalParam = aggregationInterval, evaluationGraph = evaluationGraph)
//    //      new ColorRankPrintingGlobalTerminationCondition(outConflicts, outLocMinima, extraStats, startTime, aggregationOperationParam = new IdStateMapAggregator[Int, (Int, Double)], aggregationIntervalParam = aggregationInterval, evaluationGraph = evaluationGraph)
//    //    }
//
//    val idStateMapAggregator = if (!computeRanks)
//      IdStateMapAggregator[AgentId, Int]
//    else {
//      IdStateMapAggregator[AgentId, (Int, Double)]
//    }
//
//    val initialAggregate = evaluationGraph.graph.aggregate(idStateMapAggregator)
//    println("*Initial aggregate")
//    println(initialAggregate.toMap.mkString(" "))
//
//    //    ColorPrinter(evaluationGraph).shouldTerminate(outAnimation, outConflicts, Some(outRanks), outIndConflicts, outLocMinima, extraStats, evaluationGraph.maxUtility)(initialAggregate)
//    ColorPrinter(evaluationGraph).shouldTerminate(outConflicts, outLocMinima, extraStats, evaluationGraph.maxUtility)(initialAggregate)
//
//    println("*Executing.")
//
//    val stats = evaluationGraph.graph.execute(executionConfig.withGlobalTerminationCondition(terminationCondition))
//
//    //   stats.aggregatedWorkerStatistics.numberOfOutgoingEdges
//    val finishTime = System.nanoTime
//    val executionTime = roundToMillisecondFraction(finishTime - startTime)
//
//    val conflictCount = ColorPrinter(evaluationGraph).countConflicts(evaluationGraph.graph.aggregate(idStateMapAggregator))
//
//    val utility = (evaluationGraph.maxUtility - conflictCount * 2).toDouble
//    val domainSize = evaluationGraph match {
//      case grid: Grid[_] => grid.domain.size //TODO Change these 2 lines when they will become supported.
//      case dimacsGraph: DimacsGraph[_] => dimacsGraph.domain.size
//      case other => -1
//    }
//
//    val avgGlobalUtilityRatio = extraStats.avgGlobalVsOpt.getOrElse(-1)
//    val endUtilityRatio = (evaluationGraph.maxUtility - conflictCount * 2).toDouble / evaluationGraph.maxUtility
//    val isOptimal = if (conflictCount == 0) 1 else 0
//    val timeToFirstLocOptimum = extraStats.timeToFirstLocOptimum.getOrElse(-1)
//    val messagesPerVertexPerStep = stats.aggregatedWorkerStatistics.signalMessagesReceived.toDouble / (evaluationGraph.size.toDouble * executionConfig.stepsLimit.getOrElse(1.toLong))
//    runResult += s"evaluationDescription" -> evaluationDescription //
//    runResult += s"optimizer" -> s"${optimizer1}${optimizer2}${proportion}" //
//    runResult += s"utility" -> utility.toString
//    runResult += s"domainSize" -> domainSize.toString
//    runResult += s"graphSize" -> evaluationGraph.size.toString //
//    runResult += s"executionMode" -> executionConfig.executionMode.toString //
//    runResult += s"conflictCount" -> conflictCount.toString //
//    runResult += s"avgGlobalUtilityRatio" -> avgGlobalUtilityRatio.toString // Measure (1)
//    runResult += s"endUtilityRatio" -> endUtilityRatio.toString // Measure (2)
//    runResult += s"isOptimal" -> isOptimal.toString // Measure (3)
//    runResult += s"timeToFirstLocOptimum" -> timeToFirstLocOptimum.toString // Measure (4)
//    runResult += s"messagesPerVertexPerStep" -> messagesPerVertexPerStep.toString // Measure (5)
//    runResult += s"isOptimizerRanked" -> computeRanks.toString
//    runResult += s"revision" -> revision
//    runResult += s"aggregationInterval" -> aggregationInterval.toString
//    runResult += s"run" -> runNumber.toString
//    runResult += s"stepsLimit" -> executionConfig.stepsLimit.toString
//    runResult += s"timeLimit" -> executionConfig.timeLimit.toString
//    runResult += s"graphStructure" -> evaluationGraph.toString //
//
//    runResult += s"computationTimeInMilliseconds" -> executionTime.toString //
//    runResult += s"date" -> date.toString //
//    runResult += s"executionHostname" -> java.net.InetAddress.getLocalHost.getHostName //
//
//    runResult += s"signalThreshold" -> executionConfig.signalThreshold.toString // 
//    runResult += s"collectThreshold" -> executionConfig.collectThreshold.toString //
//
//    // runResult += s"startTime" -> startTime.toString //
//    // runResult += s"endTime" -> finishTime.toString //
//
//    println("\nNumber of conflicts at the end: " + ColorPrinter(evaluationGraph).countConflicts(evaluationGraph.graph.aggregate(idStateMapAggregator)))
//    println("Shutting down.")
//    evaluationGraph.graph.shutdown
//
//    //    outAnimation.close
//    outConflicts.close
//    outLocMinima.close
//    //    if (outRanks != null)
//    //      outRanks.close
//    //    outIndConflicts.close
//
//    runResult :: finalResults
//
//  }
//}
