package com.signalcollect.dcop.graphstructures

import com.signalcollect.dcop.DcopAlgorithm
import com.signalcollect.Graph

abstract class EvaluationGraph(optimizer: DcopAlgorithm[Int, Int]) {
  def graph: Graph[Any, Any]
  def computeNeighbours(id: Int): Iterable[Int]
  def size: Int
  def maxUtility: Int //for now = number of possible satisfied constraints
  def domainForVertex(id: Int): Set[Int]
}


abstract class ConstraintEvaluationGraph(optimizer: DcopAlgorithm[Int, Int]) extends 
	EvaluationGraph(optimizer)