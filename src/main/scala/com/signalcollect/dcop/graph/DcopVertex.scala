package com.signalcollect.dcop.graph

import com.signalcollect._
import com.signalcollect.dcop.modules._

/**
 * A Dcop vertex. Description: The Vertex state is of type Config.
 *
 * @param id The Vertex Id
 * @param domain The variable Domain
 * @param optimizer The optimizer used
 * @param initialState Initial state of the vertex
 * @param debug Boolean idicating if there should be any printlines
 */
abstract class DcopVertex[Id, Action, Config <: Configuration[Id, Action], UtilityType](
  val optimizer: Optimizer[Id, Action, Config, UtilityType],
  //Doesn't work with Optimizer[...., this.Signal]
  val initialState: Config,
  debug: Boolean = false)
  extends DataGraphVertex(initialState.centralVariableAssignment._1, initialState)
  with DcopConvergenceDetection[Id, Action, Config, UtilityType] {

  type Signal = Any
  
  def changeMove(c: Config): Config = {
    val move = optimizer.computeMove(c)
    val newConfig = c.withCentralVariableAssignment(move)
    val newState = newConfig
    if (debug) {
      println(s"Vertex $id has changed its state from $state to $newState.")
    }
    newState
  }

  def collect = {
    val signalMap = mostRecentSignalMap.toMap
    //signalMap.asInstanceOf[Map[Id, Signal]]
    val neighborhoodUpdated = state.updateNeighborhood(Map.empty[Id, Signal].asInstanceOf[Map[Id, state.SignalType]])
    val c = optimizer.updateMemory(neighborhoodUpdated)
   // val c = currentConfig
    if (optimizer.shouldConsiderMove(c)) {
      changeMove(c)
    } else {
      if (debug) {
        if (isConverged(c)) {
          println(s"Vertex $id has converged and stays at move $state.")
        } else {
          println(s"Vertex $id still NOT converged, stays at move, and has $state.")
        }
      }
      c
    }
  }

}