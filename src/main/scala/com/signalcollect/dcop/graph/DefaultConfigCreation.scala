package com.signalcollect.dcop.graph

import com.signalcollect.DataGraphVertex
import com.signalcollect.dcop.impl._

/**
 * Assumes that vertex state and agent action have the same type.
 */
trait DefaultConfigCreation[Id, State] {
  this: DataGraphVertex[Id, State] with DcopConvergenceDetection[Id, State, State] =>
    
  def currentConfig: Config = {
    val neighborhood: Map[Id, State] = mostRecentSignalMap.seq.toMap.asInstanceOf[Map[Id, State]]
    val centralVariableAssignment = (id, state)
    val c = optimizer.createConfig(neighborhood, domain, centralVariableAssignment)
    c
  }
}