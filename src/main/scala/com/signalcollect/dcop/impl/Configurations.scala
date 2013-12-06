package com.signalcollect.dcop.impl

import com.signalcollect.dcop.modules._

trait DefaultConfiguration[AgentId, Action] extends ConfigurationModule[AgentId, Action] {
  this: OptimizerModule[AgentId, Action] =>

  type Config = DefaultConfig

  class DefaultConfigFactory extends ConfigFactory {
    def createConfig(neighborhood: Map[AgentId, Action],
      domain: Set[Action],
      centralVariableAssignment: (AgentId, Action)): DefaultConfig = {
      DefaultConfig(neighborhood, domain, centralVariableAssignment)
    }
  }

  case class DefaultConfig(
    val neighborhood: Map[AgentId, Action],
    val domain: Set[Action],
    val centralVariableAssignment: (AgentId, Action)) extends Configuration {
    def withCentralVariableAssignment(value: Action): DefaultConfig = {
      this.copy(centralVariableAssignment = (centralVariableAssignment._1, value))
    }
  }

}
