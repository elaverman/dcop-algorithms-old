package com.signalcollect.dcop.graph

import com.signalcollect._
import com.signalcollect.dcop.modules.Optimizer
import com.signalcollect.dcop.modules.Configuration

/**
 * The Vertex state is of type Config.
 */
trait DcopConvergenceDetection {
  this: DataGraphVertex[_,Configuration] =>

  val optimizer: Optimizer[_,_,Configuration,_]

  def isConverged(c: Configuration): Boolean = {
    optimizer.shouldTerminate(c)
  }

  def currentConfig: Configuration

  def isStateUnchanged(oldConfig: Configuration, newConfig: Configuration): Boolean = {
    oldConfig.centralVariableAssignment == newConfig.centralVariableAssignment &&
      oldConfig.neighborhood == newConfig.neighborhood
  }

  override def scoreSignal: Double = {
    if (edgesModifiedSinceSignalOperation) {
      1
    } else {
      lastSignalState match {
        case Some(oldState) => {
          if (isStateUnchanged(oldState, state) && isConverged(state)) {
            //println("=>" + state)
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
