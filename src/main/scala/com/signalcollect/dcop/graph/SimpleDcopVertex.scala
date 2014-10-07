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

class SimpleDcopVertex[Id, ActionAndState, UtilityType](
  id: Id,
  val domain: Set[ActionAndState],
  override val optimizer: Optimizer[Id, ActionAndState, SimpleConfig[Id, ActionAndState], UtilityType],
  initialState: ActionAndState,
  debug: Boolean = false)
  extends DcopVertex[Id, ActionAndState, ActionAndState, SimpleConfig[Id, ActionAndState], UtilityType](id, domain, optimizer, initialState, debug){
  
  override def configToState(c: SimpleConfig[Id, ActionAndState]): ActionAndState = c.centralVariableValue
  
  override def currentConfig: SimpleConfig[Id, ActionAndState] = {
    val neighborhood: Map[Id, ActionAndState] = mostRecentSignalMap.toMap.asInstanceOf[Map[Id, ActionAndState]]
    val centralVariableAssignment = (id, state)
    val c = SimpleConfig(neighborhood, domain, centralVariableAssignment)
    c
  }
  
}
