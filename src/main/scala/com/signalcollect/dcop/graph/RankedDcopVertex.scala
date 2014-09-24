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

class RankedVertexColoringEdge[Id](targetId: Id) extends DefaultEdge(targetId) {
  type Source = RankedDcopVertex[_, _, _]

  def signal = {
    val sourceState = source.state
    (sourceState._1, sourceState._2 / source.edgeCount)
  }
}

/** A Ranked Dcop vertex. Its state is composed by its action and its rank.ß
  *
  * @param id The Vertex Id
  * @param domain The variable Domain
  * @param optimizer The optimizer used
  * @param initialState Initial state of the vertex
  * @param debug Boolean idicating if there should be any printlines
  * @param convergeByEntireState Boolean indicating if the algorithm stops when the entire state or only the action stabilizes.
  */
class RankedDcopVertex[Id, Action, UtilityType](
  id: Id,
  val domain: Set[Action],
  override val optimizer: Optimizer[Id, Action, RankedConfig[Id, Action], UtilityType],
  initialAction: Action,
  baseRank: Double = 0.15,
  debug: Boolean = false,
  eps: Double = 0.01,
  convergeByEntireState: Boolean = true)
  extends DcopVertex[Id, (Action, Double), Action, RankedConfig[Id, Action], UtilityType](id, domain, optimizer, (initialAction, baseRank), debug) {

  type Signal = (Action, Double)

  def currentConfig: RankedConfig[Id, Action] = {
    val neighborhoodSignalMap = (mostRecentSignalMap.toMap).
      asInstanceOf[Map[Id, (Action, Double)]]
    val neighborhoodAssignments = neighborhoodSignalMap.
      map(tuple => (tuple._1, tuple._2._1)).toMap
    val neighborhoodRanks: Map[Id, Double] = neighborhoodSignalMap.
      map(tuple => (tuple._1, tuple._2._2)).toMap
    val centralVariableAssignment = (id, state._1)
    val ranks = neighborhoodRanks + ((id, state._2))
    val c = RankedConfig(neighborhoodAssignments, ranks, domain, centralVariableAssignment)
    c
  }

  def configToState(c: RankedConfig[Id, Action]): (Action, Double) = {
    val move = c.centralVariableValue
    (move, computeRankForMove(c))
  }

  def computeRankForMove(c: RankedConfig[Id, Action]): Double = {
    val allies = c.neighborhood.filter(_._2 != c.centralVariableValue)
    val allyRankSum = allies.keys.map(c.ranks).sum
    val dampingFactor = 1.0 - baseRank
    val newPageRank = baseRank + dampingFactor * allyRankSum
    newPageRank
  }
  
  override def isStateUnchanged(oldState: (Action, Double), newState: (Action, Double)): Boolean = {
    (oldState._1 == newState._1) && (math.abs(oldState._2 - newState._2) < eps)
  }
  
  override def collect = {
    val c = currentConfig
    if (optimizer.shouldConsiderMove(c)) {
      val move = optimizer.computeMove(c)
      val newConfig = c.withCentralVariableAssignment(move)
      val newState = configToState(newConfig)
      if (debug) {
        println(s"Vertex $id has changed its state from $state to $newState.")
      }
      newState
    } else {
      val newState = if (convergeByEntireState) configToState(c) else state
      if (debug) {
        if (isConverged(c)) {
          println(s"Vertex $id has converged and stays at move $newState.")
        } else {
          println(s"Vertex $id still, NOT converged, stays at move, and has $newState.")
        }
      }
      newState
    }
  }

}
