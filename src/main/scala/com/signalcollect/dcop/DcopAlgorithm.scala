package com.signalcollect.dcop

import com.signalcollect.dcop.modules._
import com.signalcollect.dcop.impl._

trait SimpleDcopAlgorithm[ConstraintParams]
  extends DcopAlgorithm[Int, Int, ConstraintParams]
  with SimpleConfiguration[Int, Int, ConstraintParams]
  with TargetFunctionsWithUtilityFunctions[Int, Int, ConstraintParams]

trait SimpleProximityDcopAlgorithm[Action, ConstraintParams]
  extends DcopAlgorithm[Int, Action, ConstraintParams]
  with SimpleConfiguration[Int, Action, ConstraintParams]
  with TargetFunctionsWithUtilityFunctions[Int, Action, ConstraintParams]

trait RankedDcopAlgorithm[ConstraintParams]
  extends RankedConfiguration[Int, Int, ConstraintParams]
  with RankedTargetFunctions[Int, Int, ConstraintParams]
  with DcopAlgorithm[Int, Int, ConstraintParams]

trait DcopAlgorithm[AgentId, Action, ConstraintParams]
  extends OptimizerModule[AgentId, Action, ConstraintParams]
  with AdjustmentSchedules[AgentId, Action, ConstraintParams]
  with DecisionRulesWithTargetFunctions[AgentId, Action, ConstraintParams]
  with TargetFunctionModule[AgentId, Action, ConstraintParams]
  with UtilityFunctions[AgentId, Action, ConstraintParams]
 
