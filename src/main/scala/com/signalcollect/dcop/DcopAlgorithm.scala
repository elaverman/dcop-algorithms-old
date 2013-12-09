package com.signalcollect.dcop

import com.signalcollect.dcop.modules._
import com.signalcollect.dcop.impl._

trait DefaultDcopAlgorithm
  extends DcopAlgorithm[Int, Int]
  with DefaultConfiguration[Int, Int]

// For the Decision Rules that are independent of other information
trait DefaultSimpleDcopAlgorithm
  extends SimpleDcopAlgorithm[Int, Int]
  with DefaultConfiguration[Int, Int]

trait DcopAlgorithm[AgentId, Action]
  extends OptimizerModule[AgentId, Action]
  with AdjustmentSchedules[AgentId, Action]
  with DecisionRulesWithTargetFunctions[AgentId, Action]
  with TargetFunctionsWithUtilityFunctions[AgentId, Action]
  with UtilityFunctions[AgentId, Action]

trait SimpleDcopAlgorithm[AgentId, Action]
  extends OptimizerModule[AgentId, Action]
  with AdjustmentSchedules[AgentId, Action]
  with DecisionRules[AgentId, Action]
