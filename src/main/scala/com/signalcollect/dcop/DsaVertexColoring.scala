package com.signalcollect.dcop

import scala.util.Random
import com.signalcollect.dcop.modules._
import com.signalcollect.dcop.impl._

case class DsaVertexColoring(changeProbability: Double)
  extends DefaultDcopAlgorithm {
  val createConfig = new DefaultConfigFactory
  val shouldConsiderMove = new ParallelRandomAdjustmentSchedule(changeProbability)
  val computeMove = new ArgmaxADecisionRule with MemoryLessTargetFunction with VertexColoringUtility
}


case object RandomVertexColoring
  extends DefaultSimpleDcopAlgorithm {
  val createConfig = new DefaultConfigFactory
  val shouldConsiderMove = new FloodAdjustmentSchedule
  val computeMove = RandomDecisionRule
}