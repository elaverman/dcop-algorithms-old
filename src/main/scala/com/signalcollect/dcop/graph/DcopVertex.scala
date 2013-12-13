package com.signalcollect.dcop.graph

import com.signalcollect._
import com.signalcollect.dcop.modules._

abstract class DcopVertex[Id, VertexState, Action](
  id: Id,
  domain: Set[Action],
  val optimizer: OptimizerModule[Id, Action],
  initialState: VertexState,
  debug: Boolean = false)
  extends DataGraphVertex(id, initialState)
  with DcopConvergenceDetection[Id, VertexState, Action] {

  def currentConfig: optimizer.Config

  def configToState(m: optimizer.Config): VertexState

  def collect = {
    val c = currentConfig
    if (optimizer.shouldConsiderMove(c)) {
      val move = optimizer.computeMove(c)
      val newConfig = c.withCentralVariableAssignment(move)
      val newState = configToState(newConfig)
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