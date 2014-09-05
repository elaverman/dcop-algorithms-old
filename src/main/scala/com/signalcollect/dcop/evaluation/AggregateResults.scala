package com.signalcollect.dcop.evaluation

import com.signalcollect.ExecutionConfiguration
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.nodeprovisioning.torque._
import java.io.File
import scala.io.Source
import com.signalcollect.dcop._
import scala.util.Random

object AggregateResults extends App {

  val numberOfRuns = 10

  val dataProportion: Double = 1.0

  val numberOfSteps = 30
 

  val numberOfVariables = 40

  val inputFilenamePrefix = "Problem-GraphColor-40_3"

  val folderPath =
    //    "krakenOutput/outputAdopt40InvSwitch"
    //    "krakenOutput/adopt"
    "allOutputCopy"

  val fileTypes = Array(

    //    "NoRankConflictDsaBVertexColoringChangeProbability0.5",
    //    "NoRankConflictDsaBVertexColoringChangeProbability0.6")
//    "NoRankConflictDsaBVertexColoringChangeProbability0.4",
//       "NoRankConflictDsaBVertexColoringChangeProbability0.5",
//        "NoRankConflictDsaBVertexColoringChangeProbability0.6",
//        "NoRankConflictDsaBVertexColoringChangeProbability0.7",
//    "NoRankConflictDsaBVertexColoringChangeProbability0.8",
//    "RankedConflictDsaBVertexColoringChangeProbability0.4",
//        "RankedConflictDsaBVertexColoringChangeProbability0.5",
//        "RankedConflictDsaBVertexColoringChangeProbability0.6",
//        "RankedConflictDsaBVertexColoringChangeProbability0.7",
//    "RankedConflictDsaBVertexColoringChangeProbability0.8",
//    "DynamicRankedConflictDsaBVertexColoringChangeProbability0.4",
//        "DynamicRankedConflictDsaBVertexColoringChangeProbability0.5",
//        "DynamicRankedConflictDsaBVertexColoringChangeProbability0.6",
//        "DynamicRankedConflictDsaBVertexColoringChangeProbability0.7",
//    "DynamicRankedConflictDsaBVertexColoringChangeProbability0.8",
//  //    //"Normal "Switches
//      "Switch1RankedConflictDsaBVertexColoringChangeProbability0.4",
//      "Switch1RankedConflictDsaBVertexColoringChangeProbability0.5",
//      "Switch1RankedConflictDsaBVertexColoringChangeProbability0.6",
//      "Switch1RankedConflictDsaBVertexColoringChangeProbability0.7",
//      "Switch1RankedConflictDsaBVertexColoringChangeProbability0.8",
//      "Switch2RankedConflictDsaBVertexColoringChangeProbability0.4",
//      "Switch2RankedConflictDsaBVertexColoringChangeProbability0.5",
//      "Switch2RankedConflictDsaBVertexColoringChangeProbability0.6",
//      "Switch2RankedConflictDsaBVertexColoringChangeProbability0.7",
//      "Switch2RankedConflictDsaBVertexColoringChangeProbability0.8",
//      "Switch3RankedConflictDsaBVertexColoringChangeProbability0.4",
//      "Switch3RankedConflictDsaBVertexColoringChangeProbability0.5",
//      "Switch3RankedConflictDsaBVertexColoringChangeProbability0.6",
//      "Switch3RankedConflictDsaBVertexColoringChangeProbability0.7",
//      "Switch3RankedConflictDsaBVertexColoringChangeProbability0.8",
//  //    //Inverted Switches
//      "SwitchInv1RankedConflictDsaBVertexColoringChangeProbability0.4",
//      "SwitchInv1RankedConflictDsaBVertexColoringChangeProbability0.5",
//      "SwitchInv1RankedConflictDsaBVertexColoringChangeProbability0.6",
//      "SwitchInv1RankedConflictDsaBVertexColoringChangeProbability0.7",
//      "SwitchInv1RankedConflictDsaBVertexColoringChangeProbability0.8",
//      "SwitchInv2RankedConflictDsaBVertexColoringChangeProbability0.4",
//      "SwitchInv2RankedConflictDsaBVertexColoringChangeProbability0.5",
//      "SwitchInv2RankedConflictDsaBVertexColoringChangeProbability0.6",
//      "SwitchInv2RankedConflictDsaBVertexColoringChangeProbability0.7",
//      "SwitchInv2RankedConflictDsaBVertexColoringChangeProbability0.8",
//      "SwitchInv3RankedConflictDsaBVertexColoringChangeProbability0.4",
//      "SwitchInv3RankedConflictDsaBVertexColoringChangeProbability0.5",
//      "SwitchInv3RankedConflictDsaBVertexColoringChangeProbability0.6",
//      "SwitchInv3RankedConflictDsaBVertexColoringChangeProbability0.7",
//      "SwitchInv3RankedConflictDsaBVertexColoringChangeProbability0.8",
//    //    //Hybrids
//    "NoRankConflictDsaBVertexColoringChangeProbability0.7RankedConflictDsaBVertexColoringChangeProbability0.70.1",
//    "NoRankConflictDsaBVertexColoringChangeProbability0.7RankedConflictDsaBVertexColoringChangeProbability0.70.3",
//    "NoRankConflictDsaBVertexColoringChangeProbability0.7RankedConflictDsaBVertexColoringChangeProbability0.70.5",
//    "NoRankConflictDsaBVertexColoringChangeProbability0.7RankedConflictDsaBVertexColoringChangeProbability0.70.7",
//    "NoRankConflictDsaBVertexColoringChangeProbability0.7RankedConflictDsaBVertexColoringChangeProbability0.70.9",
//    "NoRankConflictDsaBVertexColoringChangeProbability0.7DynamicRankedConflictDsaBVertexColoringChangeProbability0.60.1",
//    "NoRankConflictDsaBVertexColoringChangeProbability0.7DynamicRankedConflictDsaBVertexColoringChangeProbability0.60.3",
//    "NoRankConflictDsaBVertexColoringChangeProbability0.7DynamicRankedConflictDsaBVertexColoringChangeProbability0.60.5",
//    "NoRankConflictDsaBVertexColoringChangeProbability0.7DynamicRankedConflictDsaBVertexColoringChangeProbability0.60.7",
//    "NoRankConflictDsaBVertexColoringChangeProbability0.7DynamicRankedConflictDsaBVertexColoringChangeProbability0.60.9",
//    "RankedConflictDsaBVertexColoringChangeProbability0.7DynamicRankedConflictDsaBVertexColoringChangeProbability0.60.1",
//    "RankedConflictDsaBVertexColoringChangeProbability0.7DynamicRankedConflictDsaBVertexColoringChangeProbability0.60.3",
//    "RankedConflictDsaBVertexColoringChangeProbability0.7DynamicRankedConflictDsaBVertexColoringChangeProbability0.60.5",
//    "RankedConflictDsaBVertexColoringChangeProbability0.7DynamicRankedConflictDsaBVertexColoringChangeProbability0.60.7",
//    "RankedConflictDsaBVertexColoringChangeProbability0.7DynamicRankedConflictDsaBVertexColoringChangeProbability0.60.9")
    "NoRankConflictDsaBVertexColoringChangeProbability0.6RankedConflictDsaBVertexColoringChangeProbability0.50.1",
    "NoRankConflictDsaBVertexColoringChangeProbability0.6RankedConflictDsaBVertexColoringChangeProbability0.50.3",
    "NoRankConflictDsaBVertexColoringChangeProbability0.6RankedConflictDsaBVertexColoringChangeProbability0.50.5",
    "NoRankConflictDsaBVertexColoringChangeProbability0.6RankedConflictDsaBVertexColoringChangeProbability0.50.7",
    "NoRankConflictDsaBVertexColoringChangeProbability0.6RankedConflictDsaBVertexColoringChangeProbability0.50.9",
    "NoRankConflictDsaBVertexColoringChangeProbability0.6DynamicRankedConflictDsaBVertexColoringChangeProbability0.40.1",
    "NoRankConflictDsaBVertexColoringChangeProbability0.6DynamicRankedConflictDsaBVertexColoringChangeProbability0.40.3",
    "NoRankConflictDsaBVertexColoringChangeProbability0.6DynamicRankedConflictDsaBVertexColoringChangeProbability0.40.5",
    "NoRankConflictDsaBVertexColoringChangeProbability0.6DynamicRankedConflictDsaBVertexColoringChangeProbability0.40.7",
    "NoRankConflictDsaBVertexColoringChangeProbability0.6DynamicRankedConflictDsaBVertexColoringChangeProbability0.40.9",
    "RankedConflictDsaBVertexColoringChangeProbability0.5DynamicRankedConflictDsaBVertexColoringChangeProbability0.40.1",
    "RankedConflictDsaBVertexColoringChangeProbability0.5DynamicRankedConflictDsaBVertexColoringChangeProbability0.40.3",
    "RankedConflictDsaBVertexColoringChangeProbability0.5DynamicRankedConflictDsaBVertexColoringChangeProbability0.40.5",
    "RankedConflictDsaBVertexColoringChangeProbability0.5DynamicRankedConflictDsaBVertexColoringChangeProbability0.40.7",
    "RankedConflictDsaBVertexColoringChangeProbability0.5DynamicRankedConflictDsaBVertexColoringChangeProbability0.40.9")

  //    "NoRankConflictDsaBVertexColoringChangeProbability0.7SynchronousSome(100)3",
  //    "RankedConflictDsaBVertexColoringChangeProbability0.5SynchronousSome(100)3",
  //    "RankedConflictDsaBVertexColoringChangeProbability0.6SynchronousSome(100)3",
  //    "RankedConflictDsaBVertexColoringChangeProbability0.7SynchronousSome(100)3",
  //    "DynamicRankedConflictDsaBVertexColoringChangeProbability0.5SynchronousSome(100)3",
  //    "DynamicRankedConflictDsaBVertexColoringChangeProbability0.6SynchronousSome(100)3",
  //    "DynamicRankedConflictDsaBVertexColoringChangeProbability0.7SynchronousSome(100)3")

  //    "Switch2RankedConflictDsaBVertexColoringChangeProbability0.4",
  //    "Switch2RankedConflictDsaBVertexColoringChangeProbability0.5",
  //    "Switch2RankedConflictDsaBVertexColoringChangeProbability0.6",
  //    "Switch2RankedConflictDsaBVertexColoringChangeProbability0.7",
  //    "Switch2RankedConflictDsaBVertexColoringChangeProbability0.8",
  //    "SwitchRankedConflictDsaBVertexColoringChangeProbability0.4",
  //    "SwitchRankedConflictDsaBVertexColoringChangeProbability0.5",
  //    "SwitchRankedConflictDsaBVertexColoringChangeProbability0.6",
  //    "SwitchRankedConflictDsaBVertexColoringChangeProbability0.7",
  //    "SwitchRankedConflictDsaBVertexColoringChangeProbability0.8")
  //    "SwitchInv1RankedConflictDsaBVertexColoringChangeProbability0.4",
  //    "SwitchInv1RankedConflictDsaBVertexColoringChangeProbability0.6",
  //    "SwitchInv1RankedConflictDsaBVertexColoringChangeProbability0.8",
  //    "SwitchInv2RankedConflictDsaBVertexColoringChangeProbability0.4",
  //    "SwitchInv2RankedConflictDsaBVertexColoringChangeProbability0.6",
  //    "SwitchInv2RankedConflictDsaBVertexColoringChangeProbability0.8",
  //    "SwitchInv3RankedConflictDsaBVertexColoringChangeProbability0.4",
  //    "SwitchInv3RankedConflictDsaBVertexColoringChangeProbability0.6",
  //    "SwitchInv3RankedConflictDsaBVertexColoringChangeProbability0.8")

  val functions = new AggregateResultsFunctions(numberOfRuns, dataProportion, numberOfSteps, numberOfVariables, inputFilenamePrefix, folderPath, fileTypes)
 // functions.writeAverageConflictsOverTime
//  functions.writeAverageLocalMinimaOverTime
  functions.writeUtilityMeasures
  functions.writeNeMeasures

}
class AggregateResultsFunctions(numberOfRuns: Int = 10, dataProportion: Double = 1.0, numberOfSteps: Int = 100, numberOfVariables: Int = 40,
  inputFilenamePrefix: String = "Problem-GraphColor-40_3", folderPath: String = "evaluationOutput/adopt", fileTypes: Array[String]) {

  type UtilityResults = (Double, Double, Double, Double, Int)

  type NeMeasures = (Int, Int) //Did it find a NE and when

  type UtilityMeasures = (Double, Double, Int) ////   avgGlobalUtilityRatio, endUtilityRatio, isOptimal

  def addResults(arrayAggregate: Array[Long], arrayToAdd: Array[Long]) {
    if (arrayAggregate.size != arrayToAdd.size) {
      print("* Wrong size of array")
    } else {
      for (i <- 0 to numberOfSteps) { //TODO: replace this with smth functional: zip?
        arrayAggregate(i) += arrayToAdd(i)
      }
    }
  }

  //TODO write test case
  def extractResultsOverTimeFromFile(fileName: String): Array[Long] = {
    val resultsArray: Array[Long] = new Array[Long](numberOfSteps + 1) //careful with that!
    val textLines = Source.fromFile(fileName).getLines.toArray
    if (textLines.size < numberOfSteps + 1) {
      print("* File " + fileName + " has " + textLines.size + " lines instead of " + textLines.size)
    } else {
      for (i <- 0 to numberOfSteps) {
        resultsArray(i) = (textLines(i).split("\\s+"))(0).toInt
      }
    }
    resultsArray
  }

  //  //TODO: Add
  //    	MEAN, VAR (avgGlobalUtilityRatio)
  //    	MEAN, VAR (endUtilityRatio)
  //    	MEAN, VAR (isOptimal)
  //    	VAR (is NE, time to first NE)
  //    	MEAN, VAR (conflictCount at the end???)

  /**
   * Returns for one run: if a NE was found and at what step
   */
  def getNeMeasures(nashResults: Array[Long]): (Int, Int, Long) = {
    if (nashResults.size != numberOfSteps + 1) {
      println("* Array of Minima results has size " + nashResults.size + " instead of " + (numberOfSteps + 1))
    } else {
      for (i <- 0 to numberOfSteps)
        if (nashResults(i) == numberOfVariables) {
          return (1, i, nashResults(numberOfSteps))
        }
    }
    (0, -1, nashResults(numberOfSteps))
  }

  /**
   * Returns for one run: avgGlobalUtilityRatio, endUtilityRatio, isOptimal and conflictCount at the end
   */
  def getUtilityMeasures(conflictResults: Array[Long]): UtilityMeasures = {
    if (conflictResults.size != numberOfSteps + 1)
      print("* Array of conflict results has size " + conflictResults.size + " instead of " + numberOfSteps + 1)
    val optimalUtility = conflictResults(0).toDouble * 2
    val avgGlobalUtilityRatio = conflictResults.map(x => (optimalUtility.toDouble - x.toDouble * 2) / optimalUtility.toDouble).sum / (numberOfSteps + 1)
    val conflictCountAtTheEnd = conflictResults(numberOfSteps)
    val endUtilityRatio = (optimalUtility.toDouble - conflictCountAtTheEnd.toDouble * 2) / optimalUtility.toDouble
    val isOptimal = if (conflictCountAtTheEnd == 0) 1 else 0

    (avgGlobalUtilityRatio, endUtilityRatio, isOptimal)
  }
  /**
   * def online_variance(data):
   * n = 0
   * mean = 0
   * M2 = 0
   *
   * for x in data:
   * 	n = n + 1
   * 	delta = x - mean
   * 	mean = mean + delta/n
   * 	M2 = M2 + delta*(x - mean)
   *
   * variance = M2/(n - 1)
   * return variance
   */
  def writeAverageAndVariance(values: Array[Double], sizeOfData: Int) {
    var n = 0
    var mean = 0.0
    var m2 = 0.0

    for (i <- 0 until sizeOfData)
      if (values(i) != -1) {
        n += 1
        val delta = values(i) - mean
        mean = mean + delta / n
        m2 = m2 + delta * (values(i) - mean)
      }

    val variance = if (n > 1) { m2 / (n - 1) } else { -1 }
    print(mean + ", " + variance)
  }

  def writeUtilityMeasures {
    val adoptGraphFoldersList = new File(folderPath).listFiles.filter(x => x.getName.startsWith(inputFilenamePrefix)).map(_.getName)

    println("avg global utility ratios, end utility ratios, isOptimal")
    for (i <- 0 until fileTypes.size) {

      val aggregatedConflictsOverTime = new Array[Long](numberOfSteps + 1)
      var fileCount = 0
      val avgGlobalUtilityRatios = new Array[Double](500)
      val endUtilityRatios = new Array[Double](500)
      val isOptimalValues = new Array[Double](500)

      for (run <- 0 until numberOfRuns) {
        for (folder <- adoptGraphFoldersList) {
          val completePathConflicts = folderPath + "/" + folder + "/" + "conflicts" + fileTypes(i) +
            "SynchronousSome(100)3Run" + run + ".txt"
          if (new java.io.File(completePathConflicts).exists) {
            val conflictsOverTime = extractResultsOverTimeFromFile(completePathConflicts)
            avgGlobalUtilityRatios(fileCount) = getUtilityMeasures(conflictsOverTime)._1
            endUtilityRatios(fileCount) = getUtilityMeasures(conflictsOverTime)._2
            isOptimalValues(fileCount) = getUtilityMeasures(conflictsOverTime)._3
            fileCount += 1
          } else {
            println(completePathConflicts)
          }

        }
      }

      writeAverageAndVariance(avgGlobalUtilityRatios, fileCount)
      print(", ")
      writeAverageAndVariance(endUtilityRatios, fileCount)
      print(", ")
      writeAverageAndVariance(isOptimalValues, fileCount)
      println
    }
  }

  def writeNeMeasures {
    val adoptGraphFoldersList = new File(folderPath).listFiles.filter(x => x.getName.startsWith(inputFilenamePrefix)).map(_.getName)
    println("Found NE, avg number of steps")
    for (i <- 0 until fileTypes.size) {

      val aggregatedConflictsOverTime = new Array[Long](numberOfSteps + 1)
      var fileCount = 0
      val foundNe = new Array[Double](500)
      val numberOfStepsToNe = new Array[Double](500)
      val numberOfVerticesInNe = new Array[Double](500)

      for (run <- 0 until numberOfRuns) {
        for (folder <- adoptGraphFoldersList) {
          val completePathLocMinima = folderPath + "/" + folder + "/" + "locMinima" + fileTypes(i) +
            "SynchronousSome(100)3Run" + run + ".txt"
          if (new java.io.File(completePathLocMinima).exists) {
            val locMinimaOverTime = extractResultsOverTimeFromFile(completePathLocMinima)
            foundNe(fileCount) = getNeMeasures(locMinimaOverTime)._1
            numberOfStepsToNe(fileCount) = getNeMeasures(locMinimaOverTime)._2
            numberOfVerticesInNe(fileCount) = getNeMeasures(locMinimaOverTime)._3
            fileCount += 1
          } else {
            println(completePathLocMinima)
          }
        }
      }

      writeAverageAndVariance(foundNe, fileCount)
      print(", ")
      writeAverageAndVariance(numberOfStepsToNe, fileCount)
      print(", ")
      writeAverageAndVariance(numberOfVerticesInNe, fileCount)
      println
    }
  }

  def writeAverageConflictsOverTime {
    val adoptGraphFoldersList = new File(folderPath).listFiles.filter(x => x.getName.startsWith(inputFilenamePrefix)).map(_.getName)
    //println("Files " + new File(folderPath).listFiles.map(_.getName).mkString("\n"))

    println("Conflicts")
    for (i <- 0 until fileTypes.size) {
      val aggregatedConflictsOverTime = new Array[Long](numberOfSteps + 1)
      var fileCount = 0
      for (run <- 0 until numberOfRuns) {
        for (folder <- adoptGraphFoldersList) {
          val completePathConflicts = folderPath + "/" + folder + "/" + "conflicts" + fileTypes(i) +
            "SynchronousSome(100)3Run" + run + ".txt"
          //println("completePathConf " + completePathConflicts)
          if (new java.io.File(completePathConflicts).exists) {
            val conflictsOverTime = extractResultsOverTimeFromFile(completePathConflicts)
            addResults(aggregatedConflictsOverTime, conflictsOverTime)
            fileCount += 1
          } else {
            println(completePathConflicts)
          }
        }
      }

      //println("File count is: " + fileCount)
      if ((fileCount != 500) && (fileCount != 250))
        throw new Error("File count should be 500 or 250")

      for (j <- 0 to numberOfSteps) {
        print(aggregatedConflictsOverTime(j).toDouble / fileCount.toDouble + " ")
      }
      println
    }
  }

  def writeAverageLocalMinimaOverTime {
    val adoptGraphFoldersList = new File(folderPath).listFiles.filter(x => x.getName.startsWith(inputFilenamePrefix)).map(_.getName)

    println("Loc minima")
    for (i <- 0 until fileTypes.size) {
      val aggregatedConflictsOverTime = new Array[Long](numberOfSteps + 1)
      var fileCount = 0
      for (run <- 0 until numberOfRuns) {
        for (folder <- adoptGraphFoldersList) {
          val completePathLocMinima = folderPath + "/" + folder + "/" + "locMinima" + fileTypes(i) +
            "SynchronousSome(100)3Run" + run + ".txt"
          if (new java.io.File(completePathLocMinima).exists) {
            val locMinimaOverTime = extractResultsOverTimeFromFile(completePathLocMinima)
            addResults(aggregatedConflictsOverTime, locMinimaOverTime)
            fileCount += 1
          } else {
            println(completePathLocMinima)
          }
        }
      }

      //      println("File count is: " + fileCount)
            if ((fileCount != 500) && (fileCount != 250))
              throw new Error("File count should be 500 or 250")

      for (j <- 0 to numberOfSteps) {
        print(aggregatedConflictsOverTime(j).toDouble / fileCount.toDouble + " ")
      }
      println
    }
  }

}