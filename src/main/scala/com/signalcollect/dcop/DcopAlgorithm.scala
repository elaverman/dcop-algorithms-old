package com.signalcollect.dcop

import com.signalcollect.dcop.modules._
import com.signalcollect.dcop.impl._

trait SimpleDcopAlgorithm
  extends DcopAlgorithm[Int, Int]
  with SimpleConfiguration[Int, Int]
  with TargetFunctionsWithUtilityFunctions[Int, Int]

trait RankedDcopAlgorithm
  extends RankedConfiguration[Int, Int]
  with RankedTargetFunctions[Int, Int]
  with DcopAlgorithm[Int, Int]

trait DcopAlgorithm[AgentId, Action]
  extends OptimizerModule[AgentId, Action]
  with AdjustmentSchedules[AgentId, Action]
  with DecisionRulesWithTargetFunctions[AgentId, Action]
  with TargetFunctionModule[AgentId, Action]
  with UtilityFunctions[AgentId, Action]
 
