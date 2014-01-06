package com.signalcollect.dcop

import com.signalcollect.ExecutionConfiguration
import com.signalcollect.configuration.ExecutionMode

object DcopEvaluation extends App {

  //  val optimizer = DsaAVertexColoring(changeProbability = 1.0)
  //  val optimizer = DsaBVertexColoring(changeProbability = 1.0)
  val optimizers = List(
    ConflictDsaBVertexColoring(changeProbability = 0.9),
    ConflictDsaBVertexColoring(changeProbability = 0.7),
    ConflictDsaBVertexColoring(changeProbability = 0.5))
  val domains = List(
    (0 to 3).toSet)
  val widths = List(
    100)

  val execModesAggrIntervAndTermLimits = List(
    (ExecutionMode.Synchronous, 5, 1000) //,
    //(ExecutionMode.PureAsynchronous, 100, 100000L) //420000L)
    )

  var runNumber = 1
  for (optimizer <- optimizers) {
    for (domain <- domains) {
      for (width <- widths) {
        for (executionMat <- execModesAggrIntervAndTermLimits) {
          val executionConfig = executionMat._1 match {
            case ExecutionMode.Synchronous => ExecutionConfiguration(executionMat._1).withSignalThreshold(0.01).withStepsLimit(executionMat._3)
            case _ => ExecutionConfiguration(executionMat._1).withSignalThreshold(0.01).withTimeLimit(executionMat._3)
          }
          GridDcopAlgorithmRun(optimizer, domain, width, executionConfig, runNumber, executionMat._2)
        }
      }
    }
  }

}