package com.signalcollect.dcop.modules

trait DecisionRuleModule[AgentId, Action] {
  this: ConfigurationModule[AgentId, Action] =>

  type Utility = Double

  val rule: DecisionRule

  trait DecisionRule extends Serializable {
    def computeMove(c: Config): Action
    def isConverged(c: Config): Boolean
    /**
     * Delegates to isConverged by default.
     * 
     * This is so that someone who implements DecisionRule is not forced 
     * to implement this function, but can implement it if optimizations 
     * are possible given the expected utilities and the max utility. 
     * 
     * The function is also not exposed to the outside because it would 
     * leak too many implementation details to clients.
     */
    protected def isConvergedGivenUtilitiesAndMaxUtility(
      c: Config,
      expectedUtilities: Map[Action, Utility],
      maxUtility: Utility): Boolean = isConverged(c)
  }
  
  
}
