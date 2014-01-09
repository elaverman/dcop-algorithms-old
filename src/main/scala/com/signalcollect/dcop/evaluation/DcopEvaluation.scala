package com.signalcollect.dcop.evaluation

import com.signalcollect.ExecutionConfiguration
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.nodeprovisioning.torque._
import java.io.File
import scala.io.Source
import com.signalcollect.dcop._

object DcopEvaluation extends App {

  def jvmParameters = " -Xmx31000m" +
    " -Xms31000m" +
    " -XX:+AggressiveOpts" +
    " -XX:+AlwaysPreTouch" +
    " -XX:+UseNUMA" +
    " -XX:-UseBiasedLocking" +
    " -XX:MaxInlineSize=1024"

  def assemblyPath = "./target/scala-2.10/optimizers-assembly-1.0-SNAPSHOT.jar"
  val assemblyFile = new File(assemblyPath)
  val kraken = new TorqueHost(
    jobSubmitter = new TorqueJobSubmitter(username = System.getProperty("user.name"), hostname = "kraken.ifi.uzh.ch"),
    localJarPath = assemblyPath, jvmParameters = jvmParameters, jdkBinPath = "/home/user/verman/jdk1.7.0_45/bin/", priority = TorquePriority.fast)
  val localHost = new LocalHost
  val googleDocs = new GoogleDocsResultHandler(args(0), args(1), "optimizerEvaluations", "data")

  def getRevision: String = {
    try {
      val gitLogPath = ".git/logs/HEAD"
      val gitLog = new File(gitLogPath)
      val lines = Source.fromFile(gitLogPath).getLines
      val lastLine = lines.toList.last
      val revision = lastLine.split(" ")(1)
      revision
    } catch {
      case t: Throwable => "Unknown revision."
    }
  }

  /*********/
  def evalName = s"First eval."
  def runs = 1
  var evaluation = new Evaluation(evaluationName = evalName, executionHost = kraken).addResultHandler(googleDocs)
//    var evaluation = new Evaluation(evaluationName = evalName, executionHost = localHost).addResultHandler(googleDocs)
  /*********/

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
    (ExecutionMode.Synchronous, 5, 100) //,
    //(ExecutionMode.PureAsynchronous, 100, 100000L) //420000L)
    )

  for (runNumber <- (0 until runs)) {
    for (optimizer <- optimizers) {
      for (domain <- domains) {
        for (width <- widths) {
          for (executionMat <- execModesAggrIntervAndTermLimits) {
            val executionConfig = executionMat._1 match {
              case ExecutionMode.Synchronous => ExecutionConfiguration(executionMat._1).withSignalThreshold(0.01).withStepsLimit(executionMat._3)
              case _ => ExecutionConfiguration(executionMat._1).withSignalThreshold(0.01).withTimeLimit(executionMat._3)
            }
            evaluation = evaluation.addEvaluationRun(GridDcopAlgorithmRun(optimizer, domain, width, executionConfig, runNumber, executionMat._2, getRevision, evalName).runAlgorithm)
          }
        }
      }
    }
  }
  evaluation.execute

}