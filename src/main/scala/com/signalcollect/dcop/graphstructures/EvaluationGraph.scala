package com.signalcollect.dcop.graphstructures

import com.signalcollect.dcop.DcopAlgorithm
import com.signalcollect.Graph

abstract class EvaluationGraph[Action, ConstraintParams](optimizer: DcopAlgorithm[Int, Action, ConstraintParams]) {
  def graph: Graph[Any, Any]
  def computeNeighbours(id: Int): Iterable[Int]
  def size: Int
  def maxUtility: Int //for now = number of possible satisfied constraints
  def domainForVertex(id: Int): Set[Int]
}


abstract class ConstraintEvaluationGraph[ConstraintParams](optimizer: DcopAlgorithm[Int, Int, ConstraintParams]) extends 
	EvaluationGraph(optimizer)

abstract class MapGraph[ConstraintParams](optimizer: DcopAlgorithm[Int, (Int, Int), ConstraintParams]) extends 
	EvaluationGraph[(Int, Int), ConstraintParams](optimizer)