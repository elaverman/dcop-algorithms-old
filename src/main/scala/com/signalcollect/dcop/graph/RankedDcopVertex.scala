/*
 *  @author Philip Stutz
 *  @author Mihaela Verman
 *  
 *  Copyright 2013 University of Zurich
 *      
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *         http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 */

package com.signalcollect.dcop.graph
import com.signalcollect._
import com.signalcollect.dcop.modules._
import com.signalcollect.dcop.impl._

class RankedVertexColoringEdge[Id, Action, UtilityType](targetId: Id) extends DefaultEdge(targetId) {
  type Source = RankedDcopVertex[Id, Action, UtilityType]

  def signal = {
    val sourceState = source.state
    val sourceStateAssignment = source.state.centralVariableAssignment
    (sourceStateAssignment._2, sourceState.ranks(sourceStateAssignment._1) / source.edgeCount)
  }
}

/**
 * A Ranked Dcop vertex. Its state is composed by its action and its rank.ß
 *
 * @param id The Vertex Id
 * @param domain The variable Domain
 * @param optimizer The optimizer used
 * @param initialState Initial state of the vertex
 * @param debug Boolean idicating if there should be any printlines
 * @param convergeByEntireState Boolean indicating if the algorithm stops when the entire state or only the action stabilizes.
 */
class RankedDcopVertex[Id, Action, UtilityType](
  override val optimizer: Optimizer[Id, Action, RankedConfig[Id, Action], UtilityType],
  initialState: RankedConfig[Id, Action],
  baseRank: Double = 0.15,
  debug: Boolean = false,
  eps: Double = 0.01,
  convergeByEntireState: Boolean = true)
  extends DcopVertex[Id, Action, RankedConfig[Id, Action], UtilityType](optimizer, initialState, debug) {

  //Initialize (initialAction, baseRank: Double = 0.15,)

  type Signal = (Action, Double)

  def currentConfig: RankedConfig[Id, Action] = {
    val neighborhoodSignalMap = (mostRecentSignalMap.toMap).
      asInstanceOf[Map[Id, (Action, Double)]]
    val neighborhoodAssignments = neighborhoodSignalMap.
      map(tuple => (tuple._1, tuple._2._1)).toMap
    val neighborhoodRanks: Map[Id, Double] = neighborhoodSignalMap.
      map(tuple => (tuple._1, tuple._2._2)).toMap
    //  val ranks = neighborhoodRanks + ((id, state._2))
    val oldRanks = neighborhoodRanks + ((id, state.ranks(id)))
    val oldC = RankedConfig(neighborhoodAssignments, state.numberOfCollects, oldRanks, state.domain, state.centralVariableAssignment)
    val ranks = neighborhoodRanks + ((id, computeRankForMove(oldC)))
    val c = RankedConfig(neighborhoodAssignments, state.numberOfCollects + 1, ranks, state.domain, state.centralVariableAssignment)
    c
  }

  def configToState(c: RankedConfig[Id, Action]): (Action, Double) = {
    val move = c.centralVariableValue
    (move, computeRankForMove(c))
  }

  //TODO: Replace with more general.  
  def computeRankForMove(c: RankedConfig[Id, Action]): Double = {
    val allies = c.neighborhood.filter(_._2 != c.centralVariableValue)
    val allyRankSum = allies.keys.map(c.ranks).sum
    val dampingFactor = 1.0 - baseRank
    val newPageRank = baseRank + dampingFactor * allyRankSum
    newPageRank
  }

  override def isStateUnchanged(oldState: RankedConfig[Id, Action], newState: RankedConfig[Id, Action]): Boolean = {
    (oldState.centralVariableAssignment == newState.centralVariableAssignment) &&
      (math.abs(oldState.ranks(oldState.centralVariableAssignment._1) - newState.ranks(newState.centralVariableAssignment._1)) < eps)
  }

  override def collect = {
    val c = currentConfig
    if (optimizer.shouldConsiderMove(c)) {
      changeMove(c)
    } else {
      val newState = if (convergeByEntireState) c else state
      if (debug) {
        if (isConverged(c)) {
          println(s"Vertex $id has converged and stays at move $newState.")
        } else {
          println(s"Vertex $id still NOT converged, stays at move, and has $newState.")
        }
      }
      newState
    }
  }

}
