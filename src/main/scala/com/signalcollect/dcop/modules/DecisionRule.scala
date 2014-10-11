package com.signalcollect.dcop.modules

//TODO: Instead of Double replace with UtilityType or at least Numeric

trait DecisionRule[AgentId, Action, Config <: Configuration[AgentId, Action]] extends Serializable with TargetFunction[AgentId, Action, Config, Double] {
  def computeMove(c: Config): Action
  def shouldTerminate(c: Config): Boolean
  
  def isInLocalOptimum(c: Config): Boolean = {
    val expectedUtilities: Map[Action, Double] = computeExpectedUtilities(c)
    val maxUtility = expectedUtilities.values.max
    isInLocalOptimumGivenUtilitiesAndMaxUtility(c, expectedUtilities, maxUtility)
  }

  protected final def isInLocalOptimumGivenUtilitiesAndMaxUtility(
    c: Config, 
    expectedUtilities: Map[Action, Double], 
    maxUtility: Double): Boolean = {
    val currentUtility = expectedUtilities(c.centralVariableValue)
    maxUtility == currentUtility
  }
      
}
  
