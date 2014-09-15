package com.signalcollect.dcop.graph

import com.signalcollect._
import com.signalcollect.dcop.modules.Optimizer
import com.signalcollect.dcop.modules.Configuration

trait DcopConvergenceDetection[AgentId, VertexState, Action, UtilityType] {
  this: DataGraphVertex[AgentId, VertexState] =>

  protected def domain: Set[Action]
  val optimizer: Optimizer[AgentId, Action, Configuration[AgentId, Action], UtilityType]

  def isConverged(c: Configuration[AgentId, Action]): Boolean = {
    optimizer.isConverged(c)
  }

  def currentConfig: Configuration[AgentId, Action]

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
