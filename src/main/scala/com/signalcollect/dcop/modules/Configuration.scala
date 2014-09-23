package com.signalcollect.dcop.modules

trait Configuration[AgentId, Action] extends Serializable {
  def neighborhood: Map[AgentId, Action]
  def domain: Set[Action]
  def withCentralVariableAssignment(value: Action): this.type
  def centralVariableAssignment: (AgentId, Action)
  def centralVariableValue = centralVariableAssignment._2
  def computeExpectedNumberOfConflicts: Int
}

//TODO: Extend to support different rank types
trait RankedConfiguration[AgentId, Action] extends Configuration[AgentId, Action] {
  def ranks: Map[AgentId, Double]
  def withCentralVariableAssignment(value: Action): this.type
}
