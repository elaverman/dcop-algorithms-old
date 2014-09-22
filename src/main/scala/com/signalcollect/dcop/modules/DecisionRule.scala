package com.signalcollect.dcop.modules

trait DecisionRule[AgentId, Action, Config <: Configuration[AgentId, Action], UtilityType] extends Serializable {
  def computeMove(c: Config): Action
  def isConverged(c: Config): Boolean

  protected def isConvergedGivenUtilitiesAndMaxUtility(
    c: Config,
    expectedUtilities: Map[Action, UtilityType],
    maxUtility: UtilityType): Boolean = isConverged(c)
}
  
