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

package com.signalcollect.dcop

import com.signalcollect.DataGraphVertex
import com.signalcollect._
import scala.util.Random

class VertexColoringVertex(
  override val id: Int,
  val domain: Set[Int],
  val optimizer: OptimizerModule[Int, Int],
  initialState: Int) extends DataGraphVertex(id, initialState) {

  type Signal = Int

  def collect = {
    val neighborhood: Map[Int, Int] = mostRecentSignalMap.seq.toMap.asInstanceOf[Map[Int, Int]]
    val centralVariableAssignment = (id, state)
    val c = optimizer.factory.createConfig(neighborhood, domain, centralVariableAssignment)
    if (optimizer.schedule.shouldConsiderMove(c)) {
      optimizer.rule.computeMove(c)
    } else {
      state
    }
  }
}
