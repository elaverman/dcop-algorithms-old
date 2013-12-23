package com.signalcollect.dcop

import com.signalcollect.ExecutionConfiguration
import com.signalcollect.configuration.ExecutionMode

object DcopEvaluation extends App {

  //  val optimizer = DsaAVertexColoring(changeProbability = 1.0)
  //  val optimizer = DsaBVertexColoring(changeProbability = 1.0)
  val optimizers = List(
    ConflictDsaBVertexColoring(changeProbability = 1.0),
    ConflictDsaBVertexColoring(changeProbability = 0.7),
    ConflictDsaBVertexColoring(changeProbability = 0.5))
  val domains = List(
    (0 to 3).toSet)
  val widths = List(
    100)
  val executionModes = List(
    ExecutionMode.Synchronous)

  var runNumber = 1  
  for (optimizer <- optimizers) {
    for (domain <- domains) {
      for (width <- widths) {
        for (executionMode <- executionModes) {
          val executionConfig = ExecutionConfiguration(ExecutionMode.PureAsynchronous).withSignalThreshold(0.01).withTimeLimit(100000L) //(420000)
          GridDcopAlgorithmRun(optimizer, domain, width, executionConfig, runNumber)
        }
      }
    }
  }

}