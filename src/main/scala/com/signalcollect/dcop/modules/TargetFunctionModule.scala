package com.signalcollect.dcop.modules

trait TargetFunctionModule[AgentId, Action] {
  this: ConfigurationModule[AgentId, Action] =>

  trait TargetFunction {
    def computeExpectedUtilities(c: Config): Map[Action, Double]
  }
}
