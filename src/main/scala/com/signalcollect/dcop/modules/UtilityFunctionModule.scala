package com.signalcollect.dcop.modules

trait UtilityFunctionModule[AgentId, Action] {
  this: ConfigurationModule[AgentId, Action] =>

  trait UtilityFunction {
    def computeUtility(c: Config): Double
  }
}
