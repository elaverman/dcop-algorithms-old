package com.signalcollect.dcop.optimizers

import scala.util.Random
import com.signalcollect.dcop.modules._
import com.signalcollect.dcop.impl._
import com.signalcollect.dcop.impl.ArgmaxADecisionRule

class WrmiVertexColoring[AgentId, Action](changeProbability: Double, rhoValue: Double) extends Optimizer[AgentId, Action, SimpleMemoryConfig[AgentId, Action, Double], Double] {
  val schedule = new ParallelRandomAdjustmentSchedule[AgentId, Action, SimpleMemoryConfig[AgentId, Action, Double]](changeProbability)
  val rule = new LinearProbabilisticDecisionRule[AgentId, Action, SimpleMemoryConfig[AgentId, Action, Double]] 
    with NashEquilibriumConvergence[AgentId, Action, SimpleMemoryConfig[AgentId, Action, Double]] 
    with DiscountedAverageRegretsTargetFunction[AgentId, Action, SimpleMemoryConfig[AgentId, Action, Double]] 
    with VertexColoringUtility[AgentId, Action, SimpleMemoryConfig[AgentId, Action, Double]] {
    def rho = rhoValue
  }
  override def toString = "WrmiVertexColoringChangeProbability" + changeProbability + "rhoValue" + rhoValue
}
