package com.signalcollect.dcop.modules

trait TargetFunctionModule[AgentId, Action] {
  this: ConfigurationModule[AgentId, Action] =>

  trait TargetFunction extends Serializable {
    def computeExpectedUtilities(c: Config): Map[Action, Double]
  }
}
