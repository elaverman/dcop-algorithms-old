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

case class AdoptGraph(optimizer: DcopAlgorithm[Int, Int], adoptFileName: String, initialValue: (Set[Int]) => Int, debug: Boolean) extends EvaluationGraph(optimizer) {

  val textLines = Source.fromFile("adoptInput/" + adoptFileName).getLines.toList
  val constraintGraphData = getFromText(textLines)

  val constraintGraph =
    constraintGraphData.buildConstraintGraphFromData(ranked = true, optimizer, initialValue, debug)

  def getFromText(textLines: List[String]): ConstraintGraphData = {
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



  def graph = constraintGraph

  def computeNeighbours(id: Int) = constraintGraphData.neighbours.getOrElse(id, List())

  def size = constraintGraphData.neighbours.size

  def maxUtility = constraintGraphData.neighbours.map(x => x._2.size).sum

  def domainForVertex(id: Int) = constraintGraphData.possibleValues.getOrElse(id, Set())

  override def toString = adoptFileName

}