package com.signalcollect.dcop.modules

trait UtilityFunctionModule[AgentId, Action] {
  this: ConfigurationModule[AgentId, Action] =>

  trait UtilityFunction extends Serializable {
    def computeUtility(c: Config): Double
  }
}
