//package com.signalcollect.dcop.evaluation
//
//import com.signalcollect.ExecutionConfiguration
//import com.signalcollect.configuration.ExecutionMode
//import com.signalcollect.nodeprovisioning.torque._
//import java.io.File
//import scala.io.Source
//import com.signalcollect.dcop._
//import scala.util.Random
//
//object AggregateResults extends App {
//
//  type UtilityResults = (Double, Double, Double, Double, Int)
//
//  type NeMeasures = (Int, Int) //Did it find a NE and when
//
//  type UtilityMeasures = (Double, Double, Int)
//
//  val numberOfRuns = 10
//
//  val dataProportion: Double = 1.0
//
//  val numberOfSteps = 100
//
//  val numberOfVariables = 40
//
//  val inputFilenamePrefix = "Problem-GraphColor-40_3"
//
//  val folderPath =
//    "krakenOutput/outputAdopt40InvSwitch"
//  //    "krakenOutput/adopt"
//
//  val fileTypes = Array(
//    //    "NoRankConflictDsaBVertexColoringChangeProbability0.5SynchronousSome(100)3",
//    //    "NoRankConflictDsaBVertexColoringChangeProbability0.6SynchronousSome(100)3",
//    //    "NoRankConflictDsaBVertexColoringChangeProbability0.7SynchronousSome(100)3",
//    //    "RankedConflictDsaBVertexColoringChangeProbability0.5SynchronousSome(100)3",
//    //    "RankedConflictDsaBVertexColoringChangeProbability0.6SynchronousSome(100)3",
//    //    "RankedConflictDsaBVertexColoringChangeProbability0.7SynchronousSome(100)3",
//    //    "DynamicRankedConflictDsaBVertexColoringChangeProbability0.5SynchronousSome(100)3",
//    //    "DynamicRankedConflictDsaBVertexColoringChangeProbability0.6SynchronousSome(100)3",
//    //    "DynamicRankedConflictDsaBVertexColoringChangeProbability0.7SynchronousSome(100)3")
//    //    "NoRankConflictDsaBVertexColoringChangeProbability0.7DynamicRankedConflictDsaBVertexColoringChangeProbability0.60.1",
//    //    "NoRankConflictDsaBVertexColoringChangeProbability0.7DynamicRankedConflictDsaBVertexColoringChangeProbability0.60.3",
//    //    "NoRankConflictDsaBVertexColoringChangeProbability0.7DynamicRankedConflictDsaBVertexColoringChangeProbability0.60.5",
//    //    "NoRankConflictDsaBVertexColoringChangeProbability0.7DynamicRankedConflictDsaBVertexColoringChangeProbability0.60.7",
//    //    "NoRankConflictDsaBVertexColoringChangeProbability0.7DynamicRankedConflictDsaBVertexColoringChangeProbability0.60.9",
//    //    "NoRankConflictDsaBVertexColoringChangeProbability0.7RankedConflictDsaBVertexColoringChangeProbability0.70.1",
//    //    "NoRankConflictDsaBVertexColoringChangeProbability0.7RankedConflictDsaBVertexColoringChangeProbability0.70.3",
//    //    "NoRankConflictDsaBVertexColoringChangeProbability0.7RankedConflictDsaBVertexColoringChangeProbability0.70.5",
//    //    "NoRankConflictDsaBVertexColoringChangeProbability0.7RankedConflictDsaBVertexColoringChangeProbability0.70.7",
//    //    "NoRankConflictDsaBVertexColoringChangeProbability0.7RankedConflictDsaBVertexColoringChangeProbability0.70.9",
//    //    "RankedConflictDsaBVertexColoringChangeProbability0.7DynamicRankedConflictDsaBVertexColoringChangeProbability0.60.1",
//    //    "RankedConflictDsaBVertexColoringChangeProbability0.7DynamicRankedConflictDsaBVertexColoringChangeProbability0.60.3",
//    //    "RankedConflictDsaBVertexColoringChangeProbability0.7DynamicRankedConflictDsaBVertexColoringChangeProbability0.60.5",
//    //    "RankedConflictDsaBVertexColoringChangeProbability0.7DynamicRankedConflictDsaBVertexColoringChangeProbability0.60.7",
//    //    "RankedConflictDsaBVertexColoringChangeProbability0.7DynamicRankedConflictDsaBVertexColoringChangeProbability0.60.9",
//    //    "Switch2RankedConflictDsaBVertexColoringChangeProbability0.4",
//    //    "Switch2RankedConflictDsaBVertexColoringChangeProbability0.5",
//    //    "Switch2RankedConflictDsaBVertexColoringChangeProbability0.6",
//    //    "Switch2RankedConflictDsaBVertexColoringChangeProbability0.7",
//    //    "Switch2RankedConflictDsaBVertexColoringChangeProbability0.8",
//    //    "SwitchRankedConflictDsaBVertexColoringChangeProbability0.4",
//    //    "SwitchRankedConflictDsaBVertexColoringChangeProbability0.5",
//    //    "SwitchRankedConflictDsaBVertexColoringChangeProbability0.6",
//    //    "SwitchRankedConflictDsaBVertexColoringChangeProbability0.7",
//    //    "SwitchRankedConflictDsaBVertexColoringChangeProbability0.8")
//    "SwitchInv1RankedConflictDsaBVertexColoringChangeProbability0.4",
//    "SwitchInv1RankedConflictDsaBVertexColoringChangeProbability0.6",
//    "SwitchInv1RankedConflictDsaBVertexColoringChangeProbability0.8",
//    "SwitchInv2RankedConflictDsaBVertexColoringChangeProbability0.4",
//    "SwitchInv2RankedConflictDsaBVertexColoringChangeProbability0.6",
//    "SwitchInv2RankedConflictDsaBVertexColoringChangeProbability0.8",
//    "SwitchInv3RankedConflictDsaBVertexColoringChangeProbability0.4",
//    "SwitchInv3RankedConflictDsaBVertexColoringChangeProbability0.6",
//    "SwitchInv3RankedConflictDsaBVertexColoringChangeProbability0.8")
//
//  var aggregatedResults: Array[(String, Array[Long], Array[Long], (Long, Long), UtilityResults)] = fileTypes map (
//    x => (x, // The file type 
//      new Array[Long](numberOfSteps + 1), // Aggregated conflicts over time
//      new Array[Long](numberOfSteps + 1), // Aggregated number of minima over time
//      //new Array[Long](2)) // Aggregated time to first NE and Aggregated reached local NE
//      (0L, 0L),
//      (0.0, 0.0, 0.0, 0.0, 0)))
//
//  def addResults(arrayName: Array[Long], fileName: String) {
//    val textLines = Source.fromFile(fileName).getLines.toArray
//    if (textLines.size != numberOfSteps + 1) {
//      print("* File " + fileName + " has " + textLines.size + " lines instead of " + textLines.size)
//    } else {
//      for (i <- 0 to numberOfSteps) {
//        arrayName(i) += (textLines(i).split("\\s+"))(0).toInt
//      }
//    }
//  }
//
//  def addResults(arrayAggregate: Array[Long], arrayToAdd: Array[Long]) {
//    if (arrayAggregate.size != arrayToAdd.size) {
//      print("* Wrong size of array")
//    } else {
//      for (i <- 0 to numberOfSteps) { //TODO: replace this with smth functional: zip?
//        arrayAggregate(i) += arrayToAdd(i)
//      }
//    }
//  }
//
//  //TODO write test case
//  def extractResults(fileName: String): Array[Long] = {
//    val resultsArray: Array[Long] = new Array[Long](numberOfSteps + 1) //careful with that!
//    val textLines = Source.fromFile(fileName).getLines.toArray
//    if (textLines.size != numberOfSteps + 1) {
//      print("* File " + fileName + " has " + textLines.size + " lines instead of " + textLines.size)
//    } else {
//      for (i <- 0 to numberOfSteps) {
//        resultsArray(i) = (textLines(i).split("\\s+"))(0).toInt
//      }
//    }
//    resultsArray
//  }
//
//  //  //TODO: Add
//  //    	MEAN, VAR (avgGlobalUtilityRatio)
//  //    	MEAN, VAR (endUtilityRatio)
//  //    	MEAN, VAR (isOptimal)
//  //    	VAR (is NE, time to first NE)
//  //    	MEAN, VAR (conflictCount at the end???)
//
//  def getTimeToFirstNe(neResults: Array[Long], fileName: String) {
//    val textLines = Source.fromFile(fileName).getLines.toArray
//    if (textLines.size != numberOfSteps + 1) {
//      print("* File " + fileName + " has " + textLines.size + " lines instead of " + textLines.size)
//    } else {
//      for (i <- 1 to numberOfSteps + 1)
//        if ((textLines(i - 1).split("\\s+"))(0).toInt == numberOfVariables) {
//          neResults(0) += i
//          neResults(1) += 1
//        }
//    }
//  }
//
//  /**
//   * Returns for one run: if a NE was found and at what step
//   */
//  def getNeMeasures(nashResults: Array[Long]): (Int, Int) = {
//    if (nashResults.size != numberOfSteps + 1) {
//      print("* Array of Minima results has size " + nashResults.size + " instead of " + numberOfSteps + 1)
//    } else {
//      for (i <- 0 to numberOfSteps)
//        if (nashResults(i) == numberOfVariables) {
//          return (1, i)
//        }
//    }
//    (0, -1)
//  }
//
//  /**
//   * Returns for one run: avgGlobalUtilityRatio, endUtilityRatio, isOptimal and conflictCount at the end
//   */
//  def getUtilityMeasures(conflictResults: Array[Long]): UtilityMeasures = {
//    if (conflictResults.size != numberOfSteps + 1)
//      print("* Array of conflict results has size " + conflictResults.size + " instead of " + numberOfSteps + 1)
//    val optimalUtility = conflictResults(0).toDouble * 2
//    val avgGlobalUtilityRatio = conflictResults.map(x => (optimalUtility.toDouble - x.toDouble * 2) / optimalUtility.toDouble).sum / (numberOfSteps + 1)
//    val conflictCountAtTheEnd = conflictResults(numberOfSteps)
//    val endUtilityRatio = (optimalUtility.toDouble - conflictCountAtTheEnd.toDouble * 2) / optimalUtility.toDouble
//    val isOptimal = if (conflictCountAtTheEnd == 0) 1 else 0
//
//    (avgGlobalUtilityRatio, endUtilityRatio, isOptimal)
//  }
//  /**
//   * def online_variance(data):
//   * n = 0
//   * mean = 0
//   * M2 = 0
//   *
//   * for x in data:
//   * 	n = n + 1
//   * 	delta = x - mean
//   * 	mean = mean + delta/n
//   * 	M2 = M2 + delta*(x - mean)
//   *
//   * variance = M2/(n - 1)
//   * return variance
//   */
//
//  val adoptGraphFoldersList = new File(folderPath).listFiles.filter(x => x.getName.startsWith(inputFilenamePrefix)).map(_.getName)
//
//  for (folder <- adoptGraphFoldersList) {
//    //println(folder)
//
//    val fileList = new File(folderPath + "/" + folder).listFiles.map(_.getName)
//
//    if (fileList.size != aggregatedResults.size * 5 * numberOfRuns)
//      throw new Error(s"Not enough files in folder $folder")
//
//    for (file <- fileList) {
//      for (i <- 0 until aggregatedResults.size) {
//        if (file.startsWith("conflicts" + aggregatedResults(i)._1)) {
//          if (Random.nextDouble <= dataProportion) {
//            aggregatedResults(i)._2(0) += 1
//            addResults(aggregatedResults(i)._2, folderPath + "/" + folder + "/" + file) //conflicts
//          }
//        } else if (file.startsWith("locMinima" + aggregatedResults(i)._1)) {
//          if (Random.nextDouble <= dataProportion) {
//            aggregatedResults(i)._3(0) += 1
//            addResults(aggregatedResults(i)._3, folderPath + "/" + folder + "/" + file) //number of minima
//            getTimeToFirstNe(aggregatedResults(i)._4, folderPath + "/" + folder + "/" + file) //time to first NE
//          }
//        }
//      }
//    }
//  }
//
//  println("Number of files per type:")
//  for (i <- 0 until aggregatedResults.size)
//    println(aggregatedResults(i)._2(0) + " files of type " + aggregatedResults(i)._1 + ".")
//
//  println("Conflicts")
//  for (i <- 0 until aggregatedResults.size) {
//    for (j <- 1 to numberOfSteps + 1) {
//      print(aggregatedResults(i)._2(j).toDouble / aggregatedResults(i)._2(0).toDouble + " ")
//    }
//    println
//  }
//
//  println("Local Minima:")
//  for (i <- 0 until aggregatedResults.size) {
//    for (j <- 1 to numberOfSteps + 1) {
//      print(aggregatedResults(i)._3(j).toDouble / aggregatedResults(i)._2(0).toDouble + " ")
//    }
//    println
//  }
//
//  println("Time to First NE:")
//  for (i <- 0 until aggregatedResults.size) {
//    println(aggregatedResults(i)._4(0).toDouble / aggregatedResults(i)._4(1).toDouble + " ")
//  }
//
//  println("Proportion of reaching a NE:")
//  for (i <- 0 until aggregatedResults.size) {
//    println(aggregatedResults(i)._4(1).toDouble / aggregatedResults(i)._2(0).toDouble + " ")
//  }
//
//}