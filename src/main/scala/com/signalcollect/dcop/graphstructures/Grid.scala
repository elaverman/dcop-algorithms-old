package com.signalcollect.dcop.graphstructures

import com.signalcollect.GraphBuilder
import com.signalcollect.StateForwarderEdge
import com.signalcollect.dcop.graph.RankedVertexColoringEdge
import com.signalcollect.dcop.graph.SimpleDcopVertex
import com.signalcollect.dcop.graph.RankedDcopVertex
import scala.io.Source
import scala.Array.canBuildFrom
import com.signalcollect.dcop.modules._
import com.signalcollect.dcop.impl._

case class Grid[Action, Opt <: Optimizer[Int, Action, Configuration[Int, Action], Double]](optimizer: Opt, domain: Set[Action], initialValue: (Set[Action]) => Action, debug: Boolean, width: Int) extends EvaluationGraph(optimizer) {

  val g = GraphBuilder.build

  optimizer match {

    case rankedOptimizer: RankedOptimizer[Int, Action] =>
      println("Ranked Optimizer for Grid of width " + width)
      for (i <- 0 until (width * width)) {
        g.addVertex(new RankedDcopVertex[Int, Action, Double](i, domain, rankedOptimizer, initialValue(domain), debug = debug))
      }
      for (i <- 0 until width * width) {
        for (n <- computeNeighbours(i)) {
          g.addEdge(i, new RankedVertexColoringEdge(n))
        }
      }

    case simpleOptimizer: SimpleOptimizer[Int, Action] =>
      println("Simple Optimizer for Grid of width " + width)
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


