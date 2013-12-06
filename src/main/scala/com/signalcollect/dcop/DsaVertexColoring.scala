package com.signalcollect.dcop

import scala.util.Random
import com.signalcollect.dcop.modules.OptimizerModule
import com.signalcollect.dcop.impl.MemoryLessTargetFunctionModule
import com.signalcollect.dcop.impl.ArgmaxADecisionRuleModule
import com.signalcollect.dcop.impl.VertexColoringUtilityFunctionModule
import com.signalcollect.dcop.impl.ParallelRandomAdjustmentScheduleModule
import com.signalcollect.dcop.impl.DefaultConfigurationModule

class DsaVertexColoring[AgentId, Action](changeProbability: Double)
  extends OptimizerModule[AgentId, Action]
  with ParallelRandomAdjustmentScheduleModule[AgentId, Action]
  with ArgmaxADecisionRuleModule[AgentId, Action]
  with MemoryLessTargetFunctionModule[AgentId, Action]
  with VertexColoringUtilityFunctionModule[AgentId, Action]
  with DefaultConfigurationModule[AgentId, Action] {
  val createConfig = new DefaultConfigFactory
  val shouldConsiderMove = new ParallelRandomAdjustmentSchedule(changeProbability)
  val computeMove = new ArgmaxADecisionRule with MemoryLessTargetFunction with VertexColoringUtility
}
