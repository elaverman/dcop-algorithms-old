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

class RankedVertexColoringEdge(targetId: Int) extends DefaultEdge(targetId) {
  type Source = RankedDcopVertex[_, _]

  def signal = {
    val sourceState = source.state
    (sourceState._1, sourceState._2 / source.edgeCount)
  }
}

class RankedDcopVertex[Id, Action](
  id: Id,
  val domain: Set[Action],
  override val optimizer: OptimizerModule[Id, Action] with RankedConfiguration[Id, Action],
  initialAction: Action,
  baseRank: Double = 0.15,
  debug: Boolean = false)
  extends DcopVertex[Id, (Action, Double), Action](id, domain, optimizer, (initialAction, baseRank), debug)
  with RankedConfigCreation[Id, Action] {

  def configToState(c: optimizer.Config): (Action, Double) = {
    val move = c.centralVariableValue
    (move, computeRankForMove(c))
  }

  def computeRankForMove(c: optimizer.Config): Double = {
    val allies = c.neighborhood.filter(_._2 != c.centralVariableValue)
    val allyRankSum = allies.keys.map(c.ranks).sum
    val dampingFactor = 1.0 - baseRank
    val newPageRank = baseRank + dampingFactor * allyRankSum
    newPageRank
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
      if (debug) {
        if (isLocalOptimum(c)) {
          println(s"Vertex $id has converged and stays at move $state.")
        } else {
          println(s"Vertex $id still has conflicts but stays at move $state anyway.")
        }
      }
      configToState(c)
    }
  }

  override def scoreSignal: Double = {
    if (edgesModifiedSinceSignalOperation) {
      1
    } else {
      lastSignalState match {
        case Some(oldState) =>
          if (oldState._1 == state._1 && isLocalOptimum(currentConfig)) {
            0
          } else {
            1
          }
        case noStateOrStateChanged => 
          1
      }
    }
  }

}
