package com.signalcollect.dcop.modules

trait TargetFunctionModule[AgentId, Action, ConstraintParams] {
  this: ConfigurationModule[AgentId, Action,ConstraintParams ] =>

  trait TargetFunction extends Serializable {
    def computeExpectedUtilities(c: Config): Map[Action, Double]
  }
}
