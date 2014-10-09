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
import com.signalcollect.dcop.impl.SimpleConfig

class SimpleDcopVertex[Id, Action, UtilityType](
  override val optimizer: Optimizer[Id, Action, SimpleConfig[Id, Action], UtilityType],
  initialState: SimpleConfig[Id, Action],
  debug: Boolean = false)
  extends DcopVertex[Id, Action, SimpleConfig[Id, Action], UtilityType](optimizer, initialState, debug){
  
  override def currentConfig: SimpleConfig[Id, Action] = {
    val neighborhood: Map[Id, Action] = mostRecentSignalMap.toMap.asInstanceOf[Map[Id, Action]]
  //  val centralVariableAssignment = (id, state)
    val c = SimpleConfig[Id, Action](neighborhood, state.numberOfCollects+1, state.domain, state.centralVariableAssignment)
    c
  }
}
