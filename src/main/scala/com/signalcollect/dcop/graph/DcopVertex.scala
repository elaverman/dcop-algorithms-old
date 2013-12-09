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

class DcopVertex[Id, State](
  override val id: Id,
  val domain: Set[State],
  val optimizer: OptimizerModule[Id, State],
  initialState: State,
  debug: Boolean = false)
  extends DataGraphVertex(id, initialState)
  with DcopConvergenceDetection[Id, State] {

  type Signal = State

  def collect = {
    val c = currentConfig
    if (optimizer.shouldConsiderMove(c)) {
      val move = optimizer.computeMove(c)
      if (debug) {
        println(s"Vertex $id has changed its state from $state to $move.")
      }
      move
    } else {
      if (debug) {
        if (isLocalOptimum(c)) {
          println(s"Vertex $id has converged and stays at move $state.")
        }
        println(s"Vertex $id still has conflicts but stays at move $state anyway.")
      }
      state
    }
  }

}
