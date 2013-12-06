package com.signalcollect.dcop.modules

trait ConfigurationModule[AgentId, Action] {
  this: OptimizerModule[AgentId, Action] =>

  type Config <: Configuration

  trait ConfigFactory {
    def apply(neighborhood: Map[AgentId, Action],
      domain: Set[Action],
      centralVariableAssignment: (AgentId, Action)): Config = {
      createConfig(neighborhood, domain, centralVariableAssignment)
    }
    def createConfig(
      neighborhood: Map[AgentId, Action],
      domain: Set[Action],
      centralVariableAssignment: (AgentId, Action)): Config
  }

  trait Configuration {
    def neighborhood: Map[AgentId, Action]
    def domain: Set[Action]
    def withCentralVariableAssignment(value: Action): Config
    def centralVariableAssignment: (AgentId, Action)
    def centralVariableValue = centralVariableAssignment._2
  }
}
