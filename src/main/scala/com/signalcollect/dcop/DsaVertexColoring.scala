package com.signalcollect.dcop

import scala.util.Random
import com.signalcollect.dcop.modules._
import com.signalcollect.dcop.impl._

case class DsaAVertexColoring[AgentId, Action](changeProbability: Double)
  extends Optimizer[AgentId, Action, SimpleConfig[AgentId, Action], Double] {
  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
  def rule = new Object 
  		with ArgmaxADecisionRule[AgentId, Action, SimpleConfig[AgentId, Action]] 
  		with NashEquilibriumConvergence[AgentId, Action, SimpleConfig[AgentId, Action]] 
  		with MemoryLessTargetFunction[AgentId, Action, SimpleConfig[AgentId, Action], Double] 
  		with VertexColoringUtility[AgentId, Action, SimpleConfig[AgentId, Action]]
  override def toString = "DsaAVertexColoringChangeProbability" + changeProbability
}

//case class DsaBVertexColoring(changeProbability: Double)
//  extends SimpleDcopAlgorithm {
//  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
//  val rule = new ArgmaxBDecisionRule with NashEquilibriumConvergence with MemoryLessTargetFunction with VertexColoringUtility
//  override def toString = "DsaBVertexColoringChangeProbability" + changeProbability
//}
//
//case class ConflictDsaBVertexColoring(changeProbability: Double)
//  extends SimpleDcopAlgorithm {
//  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
//  val rule = new ArgmaxBDecisionRule with ZeroUtilityConvergence with MemoryLessTargetFunction with ConflictBasedVertexColoringUtility
//  override def toString = "ConflictDsaBVertexColoringChangeProbability" + changeProbability
//}
//
////case class ConflictDsaAVertexColoring(changeProbability: Double)
////  extends SimpleDcopAlgorithm {
////  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
////  val rule = new ArgmaxADecisionRule with ZeroUtilityConvergence with MemoryLessTargetFunction with ConflictBasedVertexColoringUtility
////  override def toString = "ConflictDsaAVertexColoringChangeProbability" + changeProbability
////}
//
///**
// * Ranked, RankedConflict, NoRankConflict, DynamicRankedConflict
// */
//
//
//case class RankedDsaAVertexColoring(changeProbability: Double)
//  extends RankedDcopAlgorithm {
//  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
//  val rule = new ArgmaxADecisionRule with NashEquilibriumConvergence with RankWeightedTargetFunction
//  override def toString = "RankedDsaAVertexColoringChangeProbability" + changeProbability
//}
//
//case class RankedDsaBVertexColoring(changeProbability: Double)
//  extends RankedDcopAlgorithm {
//  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
//  val rule = new ArgmaxBDecisionRule with NashEquilibriumConvergence with RankWeightedTargetFunction
//  override def toString = "RankedDsaBVertexColoringChangeProbability" + changeProbability
//}
//
//case class RankedConflictDsaBVertexColoring(changeProbability: Double)
//  extends RankedDcopAlgorithm {
//  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
//  val rule = new ArgmaxBDecisionRule with ZeroConflictConvergence with RankWeightedTargetFunction
//  override def toString = "RankedConflictDsaBVertexColoringChangeProbability" + changeProbability
//}
//
//case class RankedConflictDsaAVertexColoring(changeProbability: Double)
//  extends RankedDcopAlgorithm {
//  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
//  val rule = new ArgmaxADecisionRule with ZeroConflictConvergence with RankWeightedTargetFunction
//  override def toString = "RankedConflictDsaAVertexColoringChangeProbability" + changeProbability
//}
//
//case class NoRankConflictDsaBVertexColoring(changeProbability: Double)
//  extends RankedDcopAlgorithm with TargetFunctionsWithUtilityFunctions[Int, Int] { //the TargetFunctionsWithUtilityFunctions enables to add with MemoryLessTargetFunction and ConflictBasedVertexColoringUtility
//  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
//  val rule = new ArgmaxBDecisionRule with ZeroConflictConvergence with MemoryLessTargetFunction with ConflictBasedVertexColoringUtility
//  override def toString = "NoRankConflictDsaBVertexColoringChangeProbability" + changeProbability
//}
//
//case class NoRankConflictDsaAVertexColoring(changeProbability: Double)
//  extends RankedDcopAlgorithm with TargetFunctionsWithUtilityFunctions[Int, Int] { //the TargetFunctionsWithUtilityFunctions enables to add with MemoryLessTargetFunction and ConflictBasedVertexColoringUtility
//  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
//  val rule = new ArgmaxADecisionRule with ZeroConflictConvergence with MemoryLessTargetFunction with ConflictBasedVertexColoringUtility
//  override def toString = "NoRankConflictDsaAVertexColoringChangeProbability" + changeProbability
//}
//
//case class DynamicRankedConflictDsaBVertexColoring(changeProbability: Double)
//  extends RankedDcopAlgorithm {
//  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
//  val rule = new ArgmaxBDecisionRule with ZeroConflictConvergence with DynamicRankWeightedTargetFunction with ConflictBasedVertexColoringUtility
//  override def toString = "DynamicRankedConflictDsaBVertexColoringChangeProbability" + changeProbability
//}
//
//case class Switch1RankedConflictDsaBVertexColoring(changeProbability: Double)
//  extends RankedDcopAlgorithm {
//  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
//  val rule = new ArgmaxBDecisionRule with ZeroConflictConvergence with Switch1RankWeightedTargetFunction with ConflictBasedVertexColoringUtility
//  override def toString = "Switch1RankedConflictDsaBVertexColoringChangeProbability" + changeProbability
//}
//
//case class Switch2RankedConflictDsaBVertexColoring(changeProbability: Double)
//  extends RankedDcopAlgorithm {
//  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
//  val rule = new ArgmaxBDecisionRule with ZeroConflictConvergence with Switch2RankWeightedTargetFunction with ConflictBasedVertexColoringUtility
//  override def toString = "Switch2RankedConflictDsaBVertexColoringChangeProbability" + changeProbability
//}
//
//case class Switch3RankedConflictDsaBVertexColoring(changeProbability: Double)
//  extends RankedDcopAlgorithm {
//  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
//  val rule = new ArgmaxBDecisionRule with ZeroConflictConvergence with Switch3RankWeightedTargetFunction with ConflictBasedVertexColoringUtility
//  override def toString = "Switch3RankedConflictDsaBVertexColoringChangeProbability" + changeProbability
//}
//
//case class SwitchInv1RankedConflictDsaBVertexColoring(changeProbability: Double)
//  extends RankedDcopAlgorithm {
//  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
//  val rule = new ArgmaxBDecisionRule with ZeroConflictConvergence with SwitchInv1RankWeightedTargetFunction with ConflictBasedVertexColoringUtility
//  override def toString = "SwitchInv1RankedConflictDsaBVertexColoringChangeProbability" + changeProbability
//}
//
//case class SwitchInv2RankedConflictDsaBVertexColoring(changeProbability: Double)
//  extends RankedDcopAlgorithm {
//  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
//  val rule = new ArgmaxBDecisionRule with ZeroConflictConvergence with SwitchInv2RankWeightedTargetFunction with ConflictBasedVertexColoringUtility
//  override def toString = "SwitchInv2RankedConflictDsaBVertexColoringChangeProbability" + changeProbability
//}
//
//case class SwitchInv3RankedConflictDsaBVertexColoring(changeProbability: Double)
//  extends RankedDcopAlgorithm {
//  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
//  val rule = new ArgmaxBDecisionRule with ZeroConflictConvergence with SwitchInv3RankWeightedTargetFunction with ConflictBasedVertexColoringUtility
//  override def toString = "SwitchInv3RankedConflictDsaBVertexColoringChangeProbability" + changeProbability
//}
//
///**
// * Explorer DsaB
// */
//
//case class NoRankConflictExplorerDsaBVertexColoring(changeProbability: Double, explore: Double)
//  extends RankedDcopAlgorithm with TargetFunctionsWithUtilityFunctions[Int, Int] { //the TargetFunctionsWithUtilityFunctions enables to add with MemoryLessTargetFunction and ConflictBasedVertexColoringUtility
//  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
//  val rule = new ExplorerArgmaxBDecisionRule with ZeroConflictConvergence with MemoryLessTargetFunction with ConflictBasedVertexColoringUtility { def expl = explore }
//  override def toString = "NoRankConflictExplorerDsaBVertexColoringChangeProbability" + changeProbability + "Expl" + explore
//}
//
//case class RankedConflictExplorerDsaBVertexColoring(changeProbability: Double, explore: Double)
//  extends RankedDcopAlgorithm {
//  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
//  val rule = new ExplorerArgmaxBDecisionRule with ZeroConflictConvergence with RankWeightedTargetFunction { def expl = explore }
//  override def toString = "RankedConflictExplorerDsaBVertexColoringChangeProbability" + changeProbability + "Expl" + explore
//}
//
///**
// * RankDependentInertia
// */
//
//case class RankedConflictDsaBVertexColoringWithRankedChangeProbability(relativeChangeProbability: Double)
//  extends RankedDcopAlgorithm with RankedAdjustmentSchedules[Int, Int] {
//  val schedule = new RankedBasedAdjustmentSchedule(relativeChangeProbability)
//  val rule = new ArgmaxBDecisionRule with ZeroConflictConvergence with RankWeightedTargetFunction
//  override def toString = "RankedConflictDsaBVertexColoringWithRankedChangeProbability" + relativeChangeProbability
//}
//
//case class NoRankConflictDsaBVertexColoringWithRankedChangeProbability(relativeChangeProbability: Double)
//  extends RankedDcopAlgorithm with TargetFunctionsWithUtilityFunctions[Int, Int] with RankedAdjustmentSchedules[Int, Int] { //the TargetFunctionsWithUtilityFunctions enables to add with MemoryLessTargetFunction and ConflictBasedVertexColoringUtility
//  val schedule = new RankedBasedAdjustmentSchedule(relativeChangeProbability)
//  val rule = new ArgmaxBDecisionRule with ZeroConflictConvergence with MemoryLessTargetFunction with ConflictBasedVertexColoringUtility
//  override def toString = "NoRankConflictDsaBVertexColoringWithRankedChangeProbability" + relativeChangeProbability
//}
//
//case class RankedConflictDsaBVertexColoringWithInvertedRankedChangeProbability(relativeChangeProbability: Double)
//  extends RankedDcopAlgorithm with RankedAdjustmentSchedules[Int, Int] {
//  val schedule = new InvertRankedBasedAdjustmentSchedule(relativeChangeProbability)
//  val rule = new ArgmaxBDecisionRule with ZeroConflictConvergence with RankWeightedTargetFunction
//  override def toString = "RankedConflictDsaBVertexColoringWithInvertedRankedChangeProbability" + relativeChangeProbability
//}
//
//case class NoRankConflictDsaBVertexColoringWithInvertedRankedChangeProbability(relativeChangeProbability: Double)
//  extends RankedDcopAlgorithm with TargetFunctionsWithUtilityFunctions[Int, Int] with RankedAdjustmentSchedules[Int, Int] { //the TargetFunctionsWithUtilityFunctions enables to add with MemoryLessTargetFunction and ConflictBasedVertexColoringUtility
//  val schedule = new InvertRankedBasedAdjustmentSchedule(relativeChangeProbability)
//  val rule = new ArgmaxBDecisionRule with ZeroConflictConvergence with MemoryLessTargetFunction with ConflictBasedVertexColoringUtility
//  override def toString = "NoRankConflictDsaBVertexColoringWithInvertedRankedChangeProbability" + relativeChangeProbability
//}
//
//case class NoRankConflictDsaBVertexColoringWithDynamicRankedChangeProbability(relativeChangeProbability: Double)
//  extends RankedDcopAlgorithm with TargetFunctionsWithUtilityFunctions[Int, Int] with RankedAdjustmentSchedules[Int, Int] { //the TargetFunctionsWithUtilityFunctions enables to add with MemoryLessTargetFunction and ConflictBasedVertexColoringUtility
//  val schedule = new DynamicRankedBasedAdjustmentSchedule(relativeChangeProbability) with ConflictBasedVertexColoringUtility
//  val rule = new ArgmaxBDecisionRule with ZeroConflictConvergence with MemoryLessTargetFunction with ConflictBasedVertexColoringUtility
//  override def toString = "NoRankConflictDsaBVertexColoringWithDynamicRankedChangeProbability" + relativeChangeProbability
//}
//
//
