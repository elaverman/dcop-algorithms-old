package com.signalcollect.dcop.graph

import com.signalcollect._
import com.signalcollect.dcop.modules.OptimizerModule

trait DcopConvergenceDetection[Id, State] {
  this: DataGraphVertex[Id, State] =>

  def domain: Set[State]
  val optimizer: OptimizerModule[Id, State]

  type Config = optimizer.Config

  def isLocalOptimum(c: Config): Boolean = {
    optimizer.isLocalOptimum(c)
  }

  def currentConfig: optimizer.Config = {
    val neighborhood: Map[Id, State] = mostRecentSignalMap.seq.toMap.asInstanceOf[Map[Id, State]]
    val centralVariableAssignment = (id, state)
    val c = optimizer.createConfig(neighborhood, domain, centralVariableAssignment)
    c
  }

  override def scoreSignal: Double = {
    if (edgesModifiedSinceSignalOperation) {
      1
    } else {
      lastSignalState match {
        case Some(oldState) =>
          if (oldState == state && isLocalOptimum(currentConfig)) {
            0
          } else {
            1
          }
        case noStateOrStateChanged => 1
      }
    }
  }
}