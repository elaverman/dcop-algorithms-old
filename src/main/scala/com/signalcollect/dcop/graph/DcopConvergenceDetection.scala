package com.signalcollect.dcop.graph

import com.signalcollect._
import com.signalcollect.dcop.modules.OptimizerModule

trait DcopConvergenceDetection[Id, VertexState, AgentAction] {
  this: DataGraphVertex[Id, VertexState] =>

  protected def domain: Set[AgentAction]
  val optimizer: OptimizerModule[Id, AgentAction]

  def isConverged(c: optimizer.Config): Boolean = {
    optimizer.isConverged(c)
  }

  def currentConfig: optimizer.Config

  override def scoreSignal: Double = {
    if (edgesModifiedSinceSignalOperation) {
      1
    } else {
      lastSignalState match {
        case Some(oldState) =>
          if (oldState == state && isConverged(currentConfig)) {
            0
          } else {
            1
          }
        case noStateOrStateChanged => 1
      }
    }
  }
}