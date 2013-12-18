package com.signalcollect.dcop

import scala.util.Random
import com.signalcollect.dcop.modules._
import com.signalcollect.dcop.impl._

case class DsaAVertexColoring(changeProbability: Double)
  extends SimpleDcopAlgorithm {
  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
  val rule = new ArgmaxADecisionRule with MemoryLessTargetFunction with VertexColoringUtility
}

case class DsaBVertexColoring(changeProbability: Double)
  extends SimpleDcopAlgorithm {
  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
  val rule = new ArgmaxBDecisionRule with MemoryLessTargetFunction with VertexColoringUtility
}

case class ConflictDsaBVertexColoring(changeProbability: Double)
  extends SimpleDcopAlgorithm {
  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
  val rule = new ArgmaxBDecisionRule with MemoryLessTargetFunction with ConflictBasedVertexColoringUtility
}

case class RankedDsaAVertexColoring(changeProbability: Double)
  extends RankedDcopAlgorithm {
  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
  val rule = new ArgmaxADecisionRule with RankWeightedTargetFunction
}

case class RankedDsaBVertexColoring(changeProbability: Double)
  extends RankedDcopAlgorithm {
  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
  val rule = new ArgmaxBDecisionRule with RankWeightedTargetFunction
}