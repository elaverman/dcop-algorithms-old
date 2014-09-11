package com.signalcollect.dcop.graphstructures

import com.signalcollect.dcop.DcopAlgorithm
import com.signalcollect.dcop.modules.OptimizerModule
import com.signalcollect.GraphBuilder
import com.signalcollect.StateForwarderEdge
import com.signalcollect.dcop.graph.RankedVertexColoringEdge
import com.signalcollect.dcop.graph.SimpleDcopVertex
import com.signalcollect.dcop.graph.RankedDcopVertex
import scala.io.Source
import scala.Array.canBuildFrom
import com.signalcollect.dcop.impl.RankedConfiguration
import com.signalcollect.Graph

object ProximityGraphUtilities {

  def parseProximityFile(fileName: String): ConstraintGraphData[(Int, Int), Int] = {
    val textLines = Source.fromFile("proximityInput/" + fileName).getLines.toList
    val constraintGraphData = getFromText(textLines)
    constraintGraphData
  }

  def getFromText(textLines: List[String]): ConstraintGraphData[(Int, Int), Int] = {
    textLines match {
      case Nil => ConstraintGraphData(Map(), Map())
      case tl :: tls => {
        val splitTextLine = tl.split("\\s+")
        splitTextLine(0) match {
          case "AGENT" => getFromText(tls) //lose it
          case "VARIABLE" => {
            val variableId = splitTextLine(1).toInt
            val variableLabel: String = splitTextLine(2)
            val centerX = (splitTextLine(3).toDouble * 100).toInt
            val centerY = (splitTextLine(4).toDouble * 100).toInt
            val stepNumber = splitTextLine(5).toInt
            val xPossibleValues = ((centerX - stepNumber) to (centerX + stepNumber)).toList
            val yPossibleValues = ((centerY - stepNumber) to (centerY + stepNumber)).toList
            val variablePossibleValues: Set[(Int, Int)] = (for (x <- xPossibleValues; y <- yPossibleValues) yield (x, y)).toSet
            getFromText(tls).addPossibleValues(variableId, variablePossibleValues)
          }
          case "CONSTRAINT" => {

            val noGoods = tls.takeWhile(x => x.split("\\s+")(0) == "NOGOOD")
            val tlsRest = tls.dropWhile(x => x.split("\\s+")(0) == "NOGOOD")
            if (noGoods == Nil) throw new Error("Constraint with no NOGOOD pairs")

            val noGoodsListOfLists: List[List[Int]] = noGoods.map(x => (x.split("\\s+").drop(1).map(y => y.toInt)).toList)

            if (splitTextLine.length == noGoodsListOfLists(0).length + 1) { //no utility value specified. default 1
              val constraintVariables = splitTextLine.toList.drop(1) map (x => x.toInt)

              getFromText(tlsRest).addConstraint((constraintVariables(0), constraintVariables(1)))
            } else {

              val constraintVariables = splitTextLine.toList.drop(1).take(splitTextLine.length - 2) map (x => x.toInt)
              val utility = splitTextLine(splitTextLine.length - 1).toInt

              getFromText(tlsRest).addConstraint((constraintVariables(0), constraintVariables(1)))
            }

          }
        }
      }
    }
  }

}

trait BaseProximityGraph {

  def constraintGraphData: ConstraintGraphData[(Int, Int), Int]

  def constraintGraph: Graph[(Int, Int), (Int, Int)]

  def adoptFileName: String

  def graph = constraintGraph

  def computeNeighbours(id: Int): Set[Int] = constraintGraphData.neighbours.getOrElse(id, Set())

  def size = constraintGraphData.neighbours.size

  def maxUtility = constraintGraphData.neighbours.map(x => x._2.size).sum

  def domainForVertex(id: Int) = constraintGraphData.possibleValues.getOrElse(id, Set())

  override def toString = adoptFileName
}

case class ProximityGraph(optimizer: DcopAlgorithm[Int, (Int, Int), Int], adoptFileName: String, debug: Boolean) extends MapGraph[Int](optimizer) with BaseProximityGraph {

  val constraintGraphData = ProximityGraphUtilities.parseProximityFile(adoptFileName)

  def initialValue(initialSet: Set[(Int, Int)]) = {
    val xs = initialSet.map(_._1)
    val ys = initialSet.map(_._2)
    (((xs.max - xs.min)/2).toInt, ((ys.max - ys.min)/2).toInt)
  } 
  
  val constraintGraph = constraintGraphData.buildConstraintGraphFromData(optimizer, initialValue, debug)

}

//case class MixedProximityGraph(optimizer1: DcopAlgorithm[Int, (Int, Int), Int], optimizer2: DcopAlgorithm[Int, (Int, Int), Int], proportion: Double, adoptFileName: String, initialValue: (Set[(Int, Int)]) => (Int, Int), debug: Boolean) extends MapGraph(optimizer1) with BaseProximityGraph {
//
//  val constraintGraphData = ProximityGraphUtilities.parseProximityFile(adoptFileName)
//
//  val constraintGraph =
//    constraintGraphData.buildMixedConstraintGraphFromData(optimizer1, optimizer2, proportion, initialValue, debug)
//
//}