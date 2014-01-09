package com.signalcollect.dcop

import scala.util.Random
import com.signalcollect.dcop.modules._
import com.signalcollect.dcop.impl._

case class DsaAVertexColoring(changeProbability: Double)
  extends SimpleDcopAlgorithm {
  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
  val rule = new ArgmaxADecisionRule with NashEquilibriumConvergence with MemoryLessTargetFunction with VertexColoringUtility
  override def toString = "DsaAVertexColoringChangeProbability" + changeProbability
}

case class DsaBVertexColoring(changeProbability: Double)
  extends SimpleDcopAlgorithm {
  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
  val rule = new ArgmaxBDecisionRule with NashEquilibriumConvergence with MemoryLessTargetFunction with VertexColoringUtility
  override def toString = "DsaBVertexColoringChangeProbability" + changeProbability
}

case class ConflictDsaBVertexColoring(changeProbability: Double)
  extends SimpleDcopAlgorithm {
  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
  val rule = new ArgmaxBDecisionRule with ZeroUtilityConvergence with MemoryLessTargetFunction with ConflictBasedVertexColoringUtility
  override def toString = "ConflictDsaBVertexColoringChangeProbability" + changeProbability
}

case class RankedDsaAVertexColoring(changeProbability: Double)
  extends RankedDcopAlgorithm {
  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
  val rule = new ArgmaxADecisionRule with NashEquilibriumConvergence with RankWeightedTargetFunction
  override def toString = "RankedDsaAVertexColoringChangeProbability" + changeProbability
}

case class RankedDsaBVertexColoring(changeProbability: Double)
  extends RankedDcopAlgorithm {
  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
  val rule = new ArgmaxBDecisionRule with NashEquilibriumConvergence with RankWeightedTargetFunction
  override def toString = "RankedDsaBVertexColoringChangeProbability" + changeProbability
}

case class RankedConflictDsaBVertexColoring(changeProbability: Double)
  extends RankedDcopAlgorithm {
  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
  val rule = new ArgmaxBDecisionRule with ZeroUtilityConvergence with RankWeightedTargetFunction
  override def toString = "RankedDsaBVertexColoringChangeProbability" + changeProbability
}