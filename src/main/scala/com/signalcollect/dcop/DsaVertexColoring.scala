package com.signalcollect.dcop

import scala.util.Random
import com.signalcollect.dcop.modules._
import com.signalcollect.dcop.impl._

case class DsaVertexColoring(changeProbability: Double)
  extends SimpleDcopAlgorithm {
  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
  val rule = new ArgmaxADecisionRule with MemoryLessTargetFunction with VertexColoringUtility
}

case class RankedDsaVertexColoring(changeProbability: Double)
  extends RankedDcopAlgorithm {
  val schedule = new ParallelRandomAdjustmentSchedule(changeProbability)
  val rule = new ArgmaxADecisionRule with RankWeightedTargetFunction
}
