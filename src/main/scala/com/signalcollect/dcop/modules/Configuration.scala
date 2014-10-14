package com.signalcollect.dcop.modules

trait Configuration[AgentId, Action, SignalType] extends Serializable {
  def neighborhood: Map[AgentId, Action]
//  def numberOfCollects: Long
  def domain: Set[Action]
  def withCentralVariableAssignment(value: Action): this.type
  def centralVariableAssignment: (AgentId, Action)
  def centralVariableValue = centralVariableAssignment._2
  def computeExpectedNumberOfConflicts: Int

   
  /**
   * Returns true if the neighbourhood, the central variable assignment
   * as well as any other config specific state are similar enough for them to be considered
   * the same.
   *
   * @note numberOfCollects is not considered in this comparison, as it is purely used for
   * bookkeeping.
   */
  def isSameState(other: this.type): Boolean
  
  
  def updateNeighborhood(neighborhood: Map[AgentId, SignalType]): this.type
  
}

