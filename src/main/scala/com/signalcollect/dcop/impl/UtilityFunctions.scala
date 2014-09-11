package com.signalcollect.dcop.impl

import com.signalcollect.dcop.modules._

trait UtilityFunctions[AgentId, Action, ExtraParams] extends UtilityFunctionModule[AgentId, Action, ExtraParams] {
  this: ConfigurationModule[AgentId, Action, ExtraParams] =>

  trait VertexColoringUtility extends UtilityFunction {
    def computeUtility(c: Config) = {
      val occupiedColors = c.neighborhood.values
      val numberOfConflicts = occupiedColors.filter(_ == c.centralVariableValue).size
      val numberOfNeighbors = occupiedColors.size
      val neighborsInSync = numberOfNeighbors - numberOfConflicts
      neighborsInSync
    }
  }
  
  trait ConflictBasedVertexColoringUtility extends UtilityFunction {
    def computeUtility(c: Config) = {
      val occupiedColors = c.neighborhood.values
      val numberOfConflicts = occupiedColors.filter(_ == c.centralVariableValue).size
      - numberOfConflicts
    }
  }
  
  trait ProximityUtility extends UtilityFunction {
    def computeUtility(c: Config, time: Int) = {
      ???
      //TODO here
    }
  }
}
