package com.signalcollect.dcop.graphstructures

import com.signalcollect.dcop.modules.Optimizer
import com.signalcollect.GraphBuilder
import com.signalcollect.StateForwarderEdge
import com.signalcollect.dcop.graph.RankedVertexColoringEdge
import com.signalcollect.dcop.graph.SimpleDcopVertex
import com.signalcollect.dcop.graph.RankedDcopVertex
import scala.io.Source
import scala.Array.canBuildFrom
import com.signalcollect.dcop.impl._
import com.signalcollect.Graph
import com.signalcollect.dcop.modules.Configuration

object AdoptGraphUtilities {

  def parseAdoptFile(fileName: String): ConstraintGraphData[Int, Int] = {
    val textLines = Source.fromFile("adoptInput/" + fileName).getLines.toList
    val constraintGraphData = getFromText(textLines)
    constraintGraphData
  }

  def getFromText(textLines: List[String]): ConstraintGraphData[Int, Int] = {
    textLines match {
      case Nil => ConstraintGraphData(Map(), Map())
      case tl :: tls => {
        val splitTextLine = tl.split("\\s+")
        splitTextLine(0) match {
          case "AGENT" => getFromText(tls) //lose it
          case "VARIABLE" => {
            val variableId = splitTextLine(1).toInt
            val variablePossibleValues: Set[Int] = (0 to (splitTextLine(3).toInt - 1)).toSet
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

trait BaseAdoptGraph {

  def constraintGraphData: ConstraintGraphData[Int, Int]

  def constraintGraph: Graph[Any, Any]

  def adoptFileName: String

  def graph = constraintGraph

  def computeNeighbours(id: Int): Set[Int] = constraintGraphData.neighbours.getOrElse(id, Set())

  def size = constraintGraphData.neighbours.size

  def maxUtility = constraintGraphData.neighbours.map(x => x._2.size).sum

  def domainForVertex(id: Int) = constraintGraphData.possibleValues.getOrElse(id, Set())

  override def toString = adoptFileName
}

case class AdoptGraph[Opt <: Optimizer[Int, Int, Configuration[Int, Int], Double]](optimizer: Opt, adoptFileName: String, initialValue: (Set[Int]) => Int, debug: Boolean) extends ConstraintEvaluationGraph(optimizer) with BaseAdoptGraph {

  val constraintGraphData = AdoptGraphUtilities.parseAdoptFile(adoptFileName)

  val constraintGraph = constraintGraphData.buildConstraintGraphFromData(optimizer, initialValue, debug)

}

case class MixedAdoptGraph[Opt <: Optimizer[Int, Int, Configuration[Int, Int], Double]](optimizer1: Opt, optimizer2: Opt, proportion: Double, adoptFileName: String, initialValue: (Set[Int]) => Int, debug: Boolean) extends ConstraintEvaluationGraph(optimizer1) with BaseAdoptGraph {

  val constraintGraphData = AdoptGraphUtilities.parseAdoptFile(adoptFileName)

  val constraintGraph =
    constraintGraphData.buildMixedConstraintGraphFromData(optimizer1, optimizer2, proportion, initialValue, debug)

}