package com.signalcollect.dcop.modules

trait UtilityFunctionModule[AgentId, Action, ConstraintParams] {
  this: ConfigurationModule[AgentId, Action, ConstraintParams] =>

  trait UtilityFunction extends Serializable {
    def computeUtility(c: Config): Double
  }
}
