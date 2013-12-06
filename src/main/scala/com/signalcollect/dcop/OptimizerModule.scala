package com.signalcollect.dcop

trait OptimizerModule[AgentId, Action] {
  type Utility = Double
  type Config <: Configuration

  def utility: UtilityFunction
  def schedule: AdjustmentSchedule
  def target: TargetFunction
  def rule: DecisionRule
  def factory: ConfigFactory

  trait ConfigFactory {
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

  trait UtilityFunction {
    def apply(c: Config) = computeUtility(c)
    def computeUtility(c: Config): Utility
  }

  trait AdjustmentSchedule {
    def shouldConsiderMove(currentConfiguration: Config): Boolean
  }

  trait TargetFunction {
    def apply(c: Config) = computeExpectedUtilities(c)
    def computeExpectedUtilities(c: Config): Map[Action, Utility]
  }

  trait DecisionRule {
    def computeMove(c: Config): Action
  }

}
