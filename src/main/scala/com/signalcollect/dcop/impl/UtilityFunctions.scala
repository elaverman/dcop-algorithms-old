package com.signalcollect.dcop.impl

import com.signalcollect.dcop.modules._

trait VertexColoringUtilityFunctionModule[AgentId, Action] extends UtilityFunctionModule[AgentId, Action] {
  this: ConfigurationModule[AgentId, Action] =>

  trait VertexColoringUtility extends UtilityFunction {
    def computeUtility(c: Config) = {
      val occupiedColors = c.neighborhood.values
      val numberOfConflicts = occupiedColors.filter(_ == c.centralVariableValue).size
      val numberOfNeighbors = occupiedColors.size
      val neighborsInSync = numberOfNeighbors - numberOfConflicts
      neighborsInSync
    }
  }

}
