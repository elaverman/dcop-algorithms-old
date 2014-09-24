package com.signalcollect.dcop.graph

import com.signalcollect._
import com.signalcollect.dcop.modules._

/** A Dcop vertex. Description
  *
  * @param id The Vertex Id
  * @param domain The variable Domain
  * @param optimizer The optimizer used
  * @param initialState Initial state of the vertex
  * @param debug Boolean idicating if there should be any printlines
  */
abstract class DcopVertex[Id, VertexState, Action, Config <: Configuration[Id, Action], UtilityType](
  id: Id,
  domain: Set[Action],
  val optimizer: Optimizer[Id, Action, Config, UtilityType],
  initialState: VertexState,
  debug: Boolean = false)
  extends DataGraphVertex(id, initialState)
  with DcopConvergenceDetection[Id, VertexState, Action, Config, UtilityType] {

  def currentConfig: Config

  def configToState(m: Config): VertexState

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
        if (isConverged(c)) {
          println(s"Vertex $id has converged and stays at move $state.")
        } else {
          println(s"Vertex $id still, NOT converged, stays at move, and has $state.")
        }
      }
      state
    }
  }

}