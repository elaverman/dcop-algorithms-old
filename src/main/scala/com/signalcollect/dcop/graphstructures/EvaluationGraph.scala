package com.signalcollect.dcop.graphstructures

import com.signalcollect.dcop.impl._
import com.signalcollect.Graph
import com.signalcollect.dcop.modules.Optimizer

abstract class EvaluationGraph[AgentId, Action](optimizer: Optimizer[AgentId, Action, _, _]) {
  def graph: Graph[Any, Any]
  def computeNeighbours(id: AgentId): Iterable[AgentId]
  def size: Int
  def maxUtility: Int //for now = number of possible satisfied constraints
  def domainForVertex(id: AgentId): Set[Action]
}

abstract class ConstraintEvaluationGraph[AgentId, Action](optimizer: Optimizer[AgentId, Action, _, _]) extends EvaluationGraph[AgentId, Action](optimizer)