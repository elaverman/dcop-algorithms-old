package com.signalcollect.dcop.impl

import com.signalcollect.dcop.modules._

trait TargetFunctionsWithUtilityFunctions[AgentId, Action] extends TargetFunctionModule[AgentId, Action] {
  this: UtilityFunctionModule[AgentId, Action] with ConfigurationModule[AgentId, Action] =>

  trait MemoryLessTargetFunction extends TargetFunction {
    this: UtilityFunction => 
      
    def computeExpectedUtilities(c: Config) = {
      val configurationCandidates: Set[Config] = for {
        assignment <- c.domain
      } yield c.withCentralVariableAssignment(assignment)
      val configUtilities = configurationCandidates.map(c => (c.centralVariableValue, computeUtility(c))).toMap
      configUtilities
    }
  }
}
