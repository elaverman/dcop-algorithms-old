package com.signalcollect.dcop.graph

import com.signalcollect.DataGraphVertex
import com.signalcollect.dcop.impl._
import com.signalcollect.dcop.modules._

/**
 * Assumes that vertex state and agent action have the same type.
 */
trait RankedConfigCreation[Id, Action, ConstraintParams] {
  this: DataGraphVertex[Id, (Action, Double)] 
  with DcopConvergenceDetection[Id, (Action, Double), Action, ConstraintParams] =>
  //with RankedConfiguration[Id, Action, Rank] =>

  type Signal = (Action, Double)
  //type Optimizer = OptimizerModule[Id, Action] with RankedConfiguration[Id, Action, Rank]

  def currentConfig: optimizer.Config = {
    val neighborhoodSignalMap = (mostRecentSignalMap.toMap).
      asInstanceOf[Map[Id, (Action, Double)]]
    val neighborhoodAssignments = neighborhoodSignalMap.
      map(tuple => (tuple._1, tuple._2._1)).toMap
    val neighborhoodRanks: Map[Id, Double] = neighborhoodSignalMap.
      map(tuple => (tuple._1, tuple._2._2)).toMap
    val centralVariableAssignment = (id, state._1)
    val ranks = neighborhoodRanks + ((id, state._2))
    val c = optimizer.createNewConfig(neighborhoodAssignments, ranks, domain, centralVariableAssignment)
    c 
  }
}