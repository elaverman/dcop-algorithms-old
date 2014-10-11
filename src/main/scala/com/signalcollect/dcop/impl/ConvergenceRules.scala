package com.signalcollect.dcop.impl


import scala.util.Random
import com.signalcollect.dcop.modules._

//TODO: Once we want a different type of utility, to plug-in a Utility type param for the traits and for the expectedUtilities vals. It must be compatible with the max function.
trait NashEquilibriumConvergence[AgentId, Action, Config <: Configuration[AgentId, Action]] extends DecisionRule[AgentId, Action, Config] {

  
override def shouldTerminate(c: Config): Boolean = isInLocalOptimum(c)

}


///**
// * Is converged only when there are no more conflicts. Not based on the target or utility function.
// */
//trait ZeroConflictConvergence[AgentId, Action, Config <: Configuration[AgentId, Action]] extends DecisionRule[AgentId, Action, Config] {
//
//  /**
//   * No delegation between isConverged and isConvergedGivenUtilitiesAndMaxUtility
//   */
//  override def shouldTerminate(c: Config): Boolean = {
//    c.computeExpectedNumberOfConflicts == 0
//  }
//}
//
///**
// * Main use for hard constraints, where you have negative utility
// * for violating constraints and reach 0 utility only when no constraints are violated.
// * One example is: ConflictBasedVertexColoringUtility
// */
//trait ZeroUtilityConvergence[AgentId, Action, Config <: Configuration[AgentId, Action]] extends DecisionRule[AgentId, Action, Config] with TargetFunction[AgentId, Action, Config, Double] {
//
//  /**
//   * No delegation between shouldTerminate and isConvergedGivenUtilitiesAndMaxUtility
//   */
//  override def shouldTerminate(c: Config): Boolean = {
//    val expectedUtilities: Map[Action, Double] = computeExpectedUtilities(c)
//    val currentUtility = expectedUtilities(c.centralVariableValue)
//    currentUtility == 0
//  }
//}