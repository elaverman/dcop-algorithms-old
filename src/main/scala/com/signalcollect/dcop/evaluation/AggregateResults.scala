package com.signalcollect.dcop.evaluation

import com.signalcollect.ExecutionConfiguration
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.nodeprovisioning.torque._
import java.io.File
import scala.io.Source
import com.signalcollect.dcop._
import scala.util.Random

object AggregateResults extends App {

  val fileTypes: Array[(String, Array[Long], Array[Long])] = Array(
    ("NoRankConflictDsaBVertexColoringChangeProbability0.8", new Array(301), new Array(301)),
    ("RankedConflictDsaBVertexColoringChangeProbability0.8", new Array(301), new Array(301)),
    ("NoRankConflictDsaBVertexColoringWithRankedChangeProbability0.8", new Array(301), new Array(301)),
    ("NoRankConflictDsaBVertexColoringWithInvertedRankedChangeProbability0.8", new Array(301), new Array(301)),
    ("DynamicRankedConflictDsaBVertexColoringChangeProbability0.6", new Array(301), new Array(301)))

  //  var simple: Array[Long] = new Array(301)
  //  var ranked: Array[Long] = new Array(301) 
  //  var rInertia: Array[Long] = new Array(301)
  //  var invInertia: Array[Long] = new Array(301)

  def addResults(arrayName: Array[Long], fileName: String) {
    print("  -> ")

    val textLines = Source.fromFile(fileName).getLines.toArray
    if (textLines.size != 300) {
      print("*i* ")
    } else {
      if (Random.nextDouble < 0.8) {
        arrayName(0) += 1
        for (i <- 1 to 300) {
          arrayName(i) += (textLines(i - 1).split("\\s+"))(0).toInt
        }
      }
    }
    println("  -> " + fileName)
  }

  val adoptGraphFoldersList = new File("krakenOutput/adopt").listFiles.filter(x => x.getName.startsWith("Problem-GraphColor-40_3_3")).map(_.getName)

  for (folder <- adoptGraphFoldersList) {
    println(folder)
    val fileList = new File("krakenOutput/adopt/" + folder).listFiles.map(_.getName)

    for (file <- fileList) {
      for (i <- 0 until fileTypes.size) {
        if (file.startsWith("conflicts" + fileTypes(i)._1)) {
          addResults(fileTypes(i)._2, "krakenOutput/adopt/" + folder + "/" + file) //conflicts
          
        } else if (file.startsWith("locMinima" + fileTypes(i)._1)) {
          addResults(fileTypes(i)._2, "krakenOutput/adopt/" + folder + "/" + file) //local minima
        }
      }
    }
  }

  for (i <- 0 until fileTypes.size) {
    for (j <- 1 to 300) {
      fileTypes(i)._2(j) /= fileTypes(i)._2(0)
      print(fileTypes(i)._2(j) + " ")
    }
    println
  }

}