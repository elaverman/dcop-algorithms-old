package com.signalcollect.dcop.graphstructures

import com.signalcollect.dcop.impl._
import com.signalcollect.Graph
import com.signalcollect.dcop.modules.Optimizer

//Helper functions for gathering stats and metadata about a computation
abstract class EvaluationGraph[AgentId, Action] {
  def optimizerName: String
 // def graph: Graph[Any, Any]
 // def computeNeighbours(id: AgentId): Iterable[AgentId]
  def size: Int
  def maxUtility: Int //for now = number of possible satisfied constraints
  def domainForVertex(id: AgentId): Set[Action]
}

