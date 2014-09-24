package com.signalcollect.dcop.graph

import com.signalcollect._
import com.signalcollect.dcop.modules.Optimizer
import com.signalcollect.dcop.modules.Configuration

trait DcopConvergenceDetection[AgentId, VertexState, Action, Config <: Configuration[AgentId, Action], UtilityType] {
  this: DataGraphVertex[AgentId, VertexState] =>

  protected def domain: Set[Action]
  val optimizer: Optimizer[AgentId, Action, Config, UtilityType]

  def isConverged(c: Config): Boolean = {
    optimizer.isConverged(c)
  }

  def currentConfig: Config

  def isStateUnchanged(oldState: VertexState, newState: VertexState): Boolean = {
    oldState == newState
  }

  override def scoreSignal: Double = {
    if (edgesModifiedSinceSignalOperation) {
      1
    } else {
      lastSignalState match {
        case Some(oldState) => {
          if (isStateUnchanged(oldState, state) && isConverged(currentConfig)) {
            0
          } else {
            1
          }
        }
        case noStateOrStateChanged => 1
      }
    }
  }
}
