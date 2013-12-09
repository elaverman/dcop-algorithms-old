package com.signalcollect.dcop.graph

import com.signalcollect._
import com.signalcollect.dcop.modules._

abstract class DcopVertex[Id, VertexState, AgentAction](
  id: Id,
  val domain: Set[AgentAction],
  val optimizer: OptimizerModule[Id, AgentAction],
  initialState: VertexState,
  debug: Boolean = false)
  extends DataGraphVertex(id, initialState)
  with DcopConvergenceDetection[Id, VertexState, AgentAction] {

  def currentConfig: Config
  
  def moveToState(m: AgentAction): VertexState

  def collect = {
    val c = currentConfig
    if (optimizer.shouldConsiderMove(c)) {
      val move = optimizer.computeMove(c)
      val newState = moveToState(move)
      if (debug) {
        println(s"Vertex $id has changed its state from $state to $newState.")
      }
      newState
    } else {
      if (debug) {
        if (isLocalOptimum(c)) {
          println(s"Vertex $id has converged and stays at move $state.")
        }
        println(s"Vertex $id still has conflicts but stays at move $state anyway.")
      }
      state
    }
  }

}