package com.signalcollect.dcop.graphstructures

import com.signalcollect.GraphBuilder
import com.signalcollect.dcop.impl.RankedConfiguration
import com.signalcollect.dcop.modules.OptimizerModule
import com.signalcollect.dcop.graph.RankedDcopVertex
import com.signalcollect.dcop.DcopAlgorithm
import com.signalcollect.dcop.graph.RankedVertexColoringEdge
import com.signalcollect.dcop.graph.SimpleDcopVertex
import com.signalcollect.StateForwarderEdge
import com.signalcollect.Graph
import scala.util.Random

//TODO new format Map[Int, List[Int]]
case class ConstraintGraphData[Action, ConstraintParams](possibleValues: Map[Int, Set[Action]], neighbours: Map[Int, Set[Int]]) {

  //Retrieves the constraints for this id or an empty set if the id is not in the map
  private def getNeighbourSet(id: Int) = neighbours.getOrElse(id, Set())

  private def updatedNeighbourSet(id: Int, newNeighbour: Int): Set[Int] = {
    getNeighbourSet(id) + newNeighbour
  }

  def addPossibleValues(id: Int, values: Set[Action]): ConstraintGraphData[Action, ConstraintParams] = { //from file: VALUES var name, value0, valuen
    val newPossibleValues = this.possibleValues + ((id, values))
    val newNeighbours = neighbours + ((id, getNeighbourSet(id)))
    this.copy(possibleValues = newPossibleValues, neighbours = newNeighbours)
  }

  def addConstraint(cst: (Int, Int)): ConstraintGraphData[Action, ConstraintParams] = { //constraint, after being built from file CONSTRAINT var1, var2.../NOGOOD
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

  def buildConstraintGraphFromData(optimizer: DcopAlgorithm[Int, Action, ConstraintParams], initialValue: (Set[Action]) => Action, debug: Boolean): Graph[Any, Any] = {
    val graph = new GraphBuilder[Any, Any].build

    optimizer match {

      case rankedOptimizer: OptimizerModule[Int, Action, ConstraintParams] with RankedConfiguration[Int, Action, ConstraintParams] =>
        println("Ranked Optimizer")

        for (id <- ids) {
          val domain = possibleValues(id)
          graph.addVertex(new RankedDcopVertex(id, domain, rankedOptimizer, initialValue(domain), debug = debug))
        }

        for (id1 <- ids) {
          for (id2 <- neighbours(id1)) {
            graph.addEdge(id1, new RankedVertexColoringEdge(id2))
          }
        }

      case simpleOptimizer: OptimizerModule[Int, Action, ConstraintParams] =>
        println("Simple Optimizer")
        for (id <- ids) {
          val domain = possibleValues(id)
          graph.addVertex(new SimpleDcopVertex(id, domain, simpleOptimizer, initialValue(domain), debug = debug))
        }

        for (id1 <- ids) {
          for (id2 <- neighbours(id1)) {
            graph.addEdge(id1, new StateForwarderEdge(id2))
          }
        }
    }
    graph
  }

  def buildMixedConstraintGraphFromData(optimizer1: DcopAlgorithm[Int, Action, ConstraintParams], optimizer2: DcopAlgorithm[Int, Action, ConstraintParams], proportion: Double, initialValue: (Set[Action]) => Action, debug: Boolean): Graph[Any, Any] = {
    val graph = new GraphBuilder[Any, Any].build

    optimizer1 match {

      case rankedOptimizer1: OptimizerModule[Int, Action, ConstraintParams] with RankedConfiguration[Int, Action, ConstraintParams] => {
        optimizer2 match {
          case rankedOptimizer2: OptimizerModule[Int, Action, ConstraintParams] with RankedConfiguration[Int, Action, ConstraintParams] =>
            println(s"Ranked Optimizer 1 $proportion and Optimizer 2 ${1 - proportion}.")

            for (id <- ids) {
              val domain = possibleValues(id)
              if (Random.nextDouble <= proportion)
                graph.addVertex(new RankedDcopVertex(id, domain, rankedOptimizer1, initialValue(domain), debug = debug))
              else
                graph.addVertex(new RankedDcopVertex(id, domain, rankedOptimizer2, initialValue(domain), debug = debug))
            }

            for (id1 <- ids) {
              for (id2 <- neighbours(id1)) {
                graph.addEdge(id1, new RankedVertexColoringEdge(id2))
              }
            }

          case other => throw new Error("Graph not built. The optimizers are not both ranked.")
        }
      }

      case simpleOptimizer1: OptimizerModule[Int, Action, ConstraintParams] => {
        optimizer2 match {
          case simpleOptimizer2: OptimizerModule[Int, Action, ConstraintParams] =>
            println(s"Simple Optimizer1 $proportion and Optimizer 2 ${1 - proportion}.")

            for (id <- ids) {
              val domain = possibleValues(id)
              if (Random.nextDouble <= proportion)
                graph.addVertex(new SimpleDcopVertex(id, domain, simpleOptimizer1, initialValue(domain), debug = debug))
              else
                graph.addVertex(new SimpleDcopVertex(id, domain, simpleOptimizer2, initialValue(domain), debug = debug))
            }

            for (id1 <- ids) {
              for (id2 <- neighbours(id1)) {
                graph.addEdge(id1, new StateForwarderEdge(id2))
              }
            }

          case other => throw new Error("Graph not built. The optimizers are not both simple.")
        }
      }
    }
    graph
  }

}