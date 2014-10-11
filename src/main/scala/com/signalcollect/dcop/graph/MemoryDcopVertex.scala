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

class MemoryDcopEdge[Id](targetId: Id) extends DefaultEdge(targetId) {
  type Source = MemoryDcopVertex[_, _]

  def signal = {
    val sourceState = source.state
    sourceState.centralVariableValue
  }
}

/**
 * A Memory Dcop vertex.
 *
 * @param id The Vertex Id
 * @param domain The variable Domain
 * @param optimizer The optimizer used
 * @param initialState Initial state of the vertex
 * @param debug Boolean idicating if there should be any printlines
 * @param convergeByEntireState Boolean indicating if the algorithm stops when the entire state or only the action stabilizes.
 */
class MemoryDcopVertex[Id, Action](
  override val optimizer: Optimizer[Id, Action, SimpleMemoryConfig[Id, Action, Double], Double],
  initialState: SimpleMemoryConfig[Id, Action, Double],
  debug: Boolean = false,
  eps: Double = 0.00000000001,
  convergeByEntireState: Boolean = true)
  extends DcopVertex[Id, Action, SimpleMemoryConfig[Id, Action, Double], Double](
    optimizer, initialState, debug) {

  //Initialize state memory and stuff: (initialAction, Map.empty[Action, Double].withDefaultValue(0), 0)

  type Signal = Action //(Action, Map[Action, Double], Long)

  override def currentConfig: SimpleMemoryConfig[Id, Action, Double] = {
    val neighborhood: Map[Id, Action] = mostRecentSignalMap.toMap.asInstanceOf[Map[Id, Action]]
    val oldC = SimpleMemoryConfig(neighborhood, state.memory, state.numberOfCollects, state.domain, state.centralVariableAssignment)
    val newMemory = optimizer.rule.computeExpectedUtilities(oldC)
    val c = SimpleMemoryConfig(neighborhood, newMemory, state.numberOfCollects + 1, state.domain, state.centralVariableAssignment) //TODO???
    c
  }

  def sameMaps(newMap: Map[Action, Double], oldMap: Map[Action, Double]): Boolean = {
    for (elem1 <- newMap) {
      val inSecondMapValue = oldMap.getOrElse(elem1._1, -1.0)
      if (math.abs(elem1._2 - inSecondMapValue) > eps) return false
    }
    true
  }

  //  //TODO: Should the whole memory be the same, or only for the current action? DAAAAH!!! Same for ranked!!!
  override def isStateUnchanged(oldConfig: SimpleMemoryConfig[Id, Action, Double], newConfig: SimpleMemoryConfig[Id, Action, Double]): Boolean = {
    (oldConfig.centralVariableAssignment == newConfig.centralVariableAssignment) &&
      (oldConfig.neighborhood == newConfig.neighborhood) &&
      sameMaps(oldConfig.memory, newConfig.memory)
    //(math.abs(oldState.memory(oldState.centralVariableValue) - newState.memory(newState.centralVariableValue)) < eps)
  }

  override def collect = {
    val c = currentConfig
    if (optimizer.shouldConsiderMove(c)) {
      changeMove(c)
    } else {
      if (debug) {
        if (isConverged(c)) {
          println(s"Vertex $id has converged and stays at move $c.")
        } else {
          println(s"Vertex $id still NOT converged, stays at move, and has $c.")
        }
      }
      c
    }
  }

}
