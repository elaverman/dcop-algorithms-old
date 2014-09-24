package com.signalcollect.dcop.impl

import com.signalcollect.dcop.modules._

trait VertexColoringUtility[AgentId, Action, Config <: Configuration[AgentId, Action]] extends UtilityFunction[AgentId, Action, Config, Double] {
  def computeUtility(c: Config) = {
    val occupiedColors = c.neighborhood.values
    val numberOfConflicts = occupiedColors.filter(_ == c.centralVariableValue).size
    val numberOfNeighbors = occupiedColors.size
    val neighborsInSync = numberOfNeighbors - numberOfConflicts
    neighborsInSync
  }
}

trait ConflictBasedVertexColoringUtility[AgentId, Action, Config <: Configuration[AgentId, Action]] extends UtilityFunction[AgentId, Action, Config, Double] {
  def computeUtility(c: Config) = {
    val occupiedColors = c.neighborhood.values
    val numberOfConflicts = occupiedColors.filter(_ == c.centralVariableValue).size
    -numberOfConflicts
  }
}
