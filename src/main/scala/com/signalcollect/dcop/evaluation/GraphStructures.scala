package com.signalcollect.dcop.evaluation

import com.signalcollect.dcop.DcopAlgorithm
import com.signalcollect.Graph
import com.signalcollect.dcop.impl.RankedConfiguration
import com.signalcollect.dcop.modules.OptimizerModule
import com.signalcollect.GraphBuilder
import com.signalcollect.StateForwarderEdge
import com.signalcollect.dcop.graph.RankedVertexColoringEdge
import com.signalcollect.dcop.graph.SimpleDcopVertex
import com.signalcollect.dcop.graph.RankedDcopVertex
import scala.io.Source
import scala.annotation.tailrec

abstract class EvaluationGraph(optimizer: DcopAlgorithm[Int, Int]) {
  def graph: Graph[Any, Any]
  def computeNeighbours(id: Int): Iterable[Int]
  def size: Int
  def maxUtility: Int //for now = number of possible satisfied constraints
  def domainForVertex(id: Int): Set[Int]
}

case class Grid(optimizer: DcopAlgorithm[Int, Int], domain: Set[Int], initialValue: (Set[Int]) => Int, debug: Boolean, width: Int) extends EvaluationGraph(optimizer) {

  val g = GraphBuilder.build

  optimizer match {

    case rankedOptimizer: OptimizerModule[Int, Int] with RankedConfiguration[Int, Int] =>
      println("Ranked Optimizer")
      for (i <- 0 until width * width) {
        g.addVertex(new RankedDcopVertex(i, domain, rankedOptimizer, initialValue(domain), debug = debug))
      }
      for (i <- 0 until width * width) {
        for (n <- computeNeighbours(i)) {
          g.addEdge(i, new RankedVertexColoringEdge(n))
        }
      }

    case simpleOptimizer: OptimizerModule[Int, Int] =>
      println("Simple Optimizer")
      for (i <- 0 until width * width)
        g.addVertex(new SimpleDcopVertex(i, domain, simpleOptimizer, initialValue(domain), debug = debug))
      for (i <- 0 until width * width)
        for (n <- computeNeighbours(i))
          g.addEdge(i, new StateForwarderEdge(n))
  }

  // Returns all the neighboring cells of the cell with the given row/column
  def potentialNeighbours(column: Int, row: Int): List[(Int, Int)] = {
    List(
      (column - 1, row - 1), (column, row - 1), (column + 1, row - 1),
      (column - 1, row), (column + 1, row),
      (column - 1, row + 1), (column, row + 1), (column + 1, row + 1))
  }

  // Tests if a cell is within the grid boundaries
  def inGrid(column: Int, row: Int): Boolean = {
    column >= 0 && row >= 0 && column < width && row < width
  }

  def computeNeighbours(id: Int): Iterable[Int] = {
    val column: Int = id % width
    val row: Int = id / width
    potentialNeighbours(column, row).filter(coordinate => inGrid(coordinate._1, coordinate._2)) map
      (coordinate => (coordinate._2 * width + coordinate._1))
  }

  def graph = g

  def size = width * width

  def maxUtility = (width - 2) * (width - 2) * 8 + (width - 2) * 20 + 12
  
  def domainForVertex(id: Int) = domain

  override def toString = "Grid" + size.toString
}

//TODO new format Map[Int, List[Int]]
case class ConstraintGraphData(possibleValues: Map[Int, Set[Int]], neighbours: Map[Int, Set[Int]]) {

  //Retrieves the constraints for this id or an empty set if the id is not in the map
  private def getNeighbourSet(id: Int) = neighbours.getOrElse(id, Set())

  private def updatedNeighbourSet(id: Int, newNeighbour: Int): Set[Int] = {
    getNeighbourSet(id) + newNeighbour
  }

  def addPossibleValues(id: Int, values: Set[Int]): ConstraintGraphData = { //from file: VALUES var name, value0, valuen
    val newPossibleValues = this.possibleValues + ((id, values))
    val newNeighbours = neighbours + ((id, getNeighbourSet(id)))
    this.copy(possibleValues = newPossibleValues, neighbours = newNeighbours)
  }

  def addConstraint(cst: (Int, Int)): ConstraintGraphData = { //constraint, after being built from file CONSTRAINT var1, var2.../NOGOOD
    val (id1, id2) = cst
    val newNeighbours = neighbours +
      ((id1, updatedNeighbourSet(id1, id2))) +
      ((id2, updatedNeighbourSet(id2, id1)))
    this.copy(neighbours = newNeighbours)
  }

  def ids = neighbours.keys

  override def toString = {
    "All Variables = " + neighbours.keys +
      "\n Possible values of variables = " + possibleValues.map(x => x._1 + "-> [" + x._2.mkString(" ") + "]").mkString("; ") +
      "\n Constraints: \n" + neighbours.mkString("\n")
  }
}

case class AdoptGraph(optimizer: DcopAlgorithm[Int, Int], adoptFileName: String, initialValue: (Set[Int]) => Int, debug: Boolean) extends EvaluationGraph(optimizer) {

  val textLines = Source.fromFile("adoptInput/" + adoptFileName).getLines.toList
  val constraintGraphData = getFromText(textLines)
  
  val constraintGraph =
    buildConstraintGraphFromData(constraintGraphData, ranked = true, optimizer)

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

  def buildConstraintGraphFromData(constraintGraphData: ConstraintGraphData, ranked: Boolean, optimizer: DcopAlgorithm[Int, Int]): Graph[Any, Any] = {
    val graph = new GraphBuilder[Any, Any].build

    optimizer match {

      case rankedOptimizer: OptimizerModule[Int, Int] with RankedConfiguration[Int, Int] =>
        println("Ranked Optimizer")

        for (id <- constraintGraphData.ids) {
          val domain = constraintGraphData.possibleValues(id)
          graph.addVertex(new RankedDcopVertex(id, domain, rankedOptimizer, initialValue(domain), debug = debug))
        }

        for (id1 <- constraintGraphData.ids) {
          for (id2 <- constraintGraphData.neighbours(id1)) {
            graph.addEdge(id1, new RankedVertexColoringEdge(id2))
          }
        }

      case simpleOptimizer: OptimizerModule[Int, Int] =>
        println("Simple Optimizer")
        for (id <- constraintGraphData.ids) {
          val domain = constraintGraphData.possibleValues(id)
          graph.addVertex(new SimpleDcopVertex(id, domain, simpleOptimizer, initialValue(domain), debug = debug))
        }

        for (id1 <- constraintGraphData.ids) {
          for (id2 <- constraintGraphData.neighbours(id1)) {
            graph.addEdge(id1, new StateForwarderEdge(id2))
          }
        }
    }
    graph
  }

  def graph = constraintGraph

  def computeNeighbours(id: Int) = constraintGraphData.neighbours.getOrElse(id, List())

  def size = constraintGraphData.neighbours.size
  
  def maxUtility = constraintGraphData.neighbours.map(x => x._2.size).sum
  
  def domainForVertex(id: Int) = constraintGraphData.possibleValues.getOrElse(id, Set())

  override def toString = adoptFileName

}
