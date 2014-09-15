package com.signalcollect.dcop.evaluation

import com.signalcollect.ExecutionConfiguration
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.nodeprovisioning.torque._
import java.io.File
import scala.io.Source
import com.signalcollect.dcop._
import scala.util.Random
import scala.slick.lifted.TableQuery
import java.net._
import com.signalcollect.nodeprovisioning.slurm._
import com.signalcollect.dcop.modules._
import com.signalcollect.dcop.impl._

object DcopEvaluation extends App {

  def jvmParameters = " -Xmx10240m" +
    // " -Xms512m" +
    " -XX:+AggressiveOpts" +
    " -XX:+AlwaysPreTouch" +
    " -XX:+UseNUMA" +
    " -XX:-UseBiasedLocking" +
    " -XX:MaxInlineSize=1024"

  def assemblyPath = "./target/scala-2.10/optimizers-assembly-1.0-SNAPSHOT.jar"
  val assemblyFile = new File(assemblyPath)
  val kraken = new TorqueHost(
    jobSubmitter = new TorqueJobSubmitter(username = System.getProperty("user.name"), hostname = "kraken.ifi.uzh.ch"),
    coresPerNode = 23,
    localJarPath = assemblyPath, jvmParameters = jvmParameters, jdkBinPath = "/home/user/verman/jdk1.7.0_45/bin/", priority = TorquePriority.fast)
  val gru = new SlurmHost(
    jobSubmitter = new SlurmJobSubmitter(username = System.getProperty("user.name"), hostname = "gru.ifi.uzh.ch"),
    coresPerNode = 10,
    localJarPath = assemblyPath, jvmParameters = jvmParameters, jdkBinPath = "/home/user/verman/jdk1.7.0_45/bin/")
  val localHost = new LocalHost
  val googleDocs = new GoogleDocsResultHandler(args(0), args(1), "optimizerEvaluations", "Ranked")
  val mySql = new MySqlResultHandler(args(2), args(3), args(4))

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

  def randomFromDomain(domain: Set[Int]) = domain.toSeq(Random.nextInt(domain.size))
  def zeroInitialized(domain: Set[Int]) = 0
  val debug = false

  /*********/
  def evalName = s"BigEval gru"
  def evalNumber = 8
  def runs = 10
  def pure = false
  //  var evaluation = new Evaluation(evaluationName = evalName, evaluationNumber = evalNumber, executionHost = kraken).addResultHandler(mySql)
  var evaluation = new Evaluation(evaluationName = evalName, evaluationNumber = evalNumber, executionHost = gru) //.addResultHandler(mySql)
  //        var evaluation = new Evaluation(evaluationName = evalName, executionHost = localHost).addResultHandler(mySql)
  /*********/

  //TODO Why do we have the probl since we SimpleConfig used by DsaAVC extends Configuration??
  val optimizers: List[Optimizer[Int, Int, Configuration[Int, Int], Double]] = List(
    DsaAVertexColoring[Int, Int](changeProbability = 0.4))

  //TODO Put back in the mixed optimizers
  //  val optimizerPairs: List[(DcopAlgorithm[Int, Int], DcopAlgorithm[Int, Int])] = List(
  //    (NoRankConflictDsaBVertexColoring(changeProbability = 0.6), DynamicRankedConflictDsaBVertexColoring(changeProbability = 0.4)),
  //    (RankedConflictDsaBVertexColoring(changeProbability = 0.5), DynamicRankedConflictDsaBVertexColoring(changeProbability = 0.4)),
  //    (NoRankConflictDsaBVertexColoring(changeProbability = 0.6), RankedConflictDsaBVertexColoring(changeProbability = 0.5)))

  val proportions = List(0.1, 0.3, 0.5, 0.7, 0.9)

  val execModesAggrIntervAndTermLimits = List(
    //   (ExecutionMode.PureAsynchronous, 30000, 3600000), //100, 100000) //420000L)
    (ExecutionMode.Synchronous, 1, 100) //30, 3600) //5, 800),
    )

  val adoptGraphNamesList = new java.io.File("adoptInput").listFiles.filter(x => (x.getName.startsWith("Problem-GraphColor-40_3_"))).map(_.getName)
  val dimacsGraphNamesList = new java.io.File("dimacsInput").listFiles.filter(x => (x.getName.endsWith("flat1000_76_0.col"))).map(_.getName)

  if (pure) {
    /**
     * Pure evaluation
     */
    for (runNumber <- (0 until runs)) {
      for (optimizer <- optimizers) {
        val evaluationGraphs = //List(GridParameters((0 to 9).toSet, zeroInitialized, debug, 1000)) // ++
          adoptGraphNamesList.map(x => AdoptGraphParameters(x, zeroInitialized, debug)) // ++
        //        dimacsGraphNamesList.map(x => DimacsGraphParameters(x, (0 to 3).toSet, zeroInitialized, debug)) ++
        //          dimacsGraphNamesList.map(x => DimacsGraphParameters(x, (0 to 75).toSet, zeroInitialized, debug)) ++
        //          dimacsGraphNamesList.map(x => DimacsGraphParameters(x, (0 to 99).toSet, zeroInitialized, debug))
        for (evaluationGraph <- evaluationGraphs) {
          for (executionMat <- execModesAggrIntervAndTermLimits) {
            val executionConfig = executionMat._1 match {
              case ExecutionMode.Synchronous => ExecutionConfiguration(executionMat._1).withSignalThreshold(0.01).withStepsLimit(executionMat._3)
              case _ => ExecutionConfiguration(executionMat._1).withSignalThreshold(0.01).withTimeLimit(executionMat._3)
            }
            evaluation = evaluation.addEvaluationRun(DcopAlgorithmRun(optimizer, evaluationGraph, executionConfig, runNumber, executionMat._2, getRevision, evalName).runAlgorithm)
          }
        }
      }
    }
  } else {
    //TODO: put back in the mixed optimizers
    //    /**
    //     * Mixed algorithms evaluation
    //     */
    //
    //    for (runNumber <- (0 until runs)) {
    //      for (optimizerPair <- optimizerPairs) {
    //        for (proportion <- proportions) {
    //          val evaluationGraphs = //List(GridParameters((0 to 9).toSet, zeroInitialized, debug, 1000)) // ++
    //            adoptGraphNamesList.map(x => AdoptGraphParameters(x, zeroInitialized, debug)) // ++
    //          //        dimacsGraphNamesList.map(x => DimacsGraphParameters(x, (0 to 3).toSet, zeroInitialized, debug)) ++
    //          //          dimacsGraphNamesList.map(x => DimacsGraphParameters(x, (0 to 75).toSet, zeroInitialized, debug)) ++
    //          //          dimacsGraphNamesList.map(x => DimacsGraphParameters(x, (0 to 99).toSet, zeroInitialized, debug))
    //          for (evaluationGraph <- evaluationGraphs) {
    //            for (executionMat <- execModesAggrIntervAndTermLimits) {
    //              val executionConfig = executionMat._1 match {
    //                case ExecutionMode.Synchronous => ExecutionConfiguration(executionMat._1).withSignalThreshold(0.01).withStepsLimit(executionMat._3)
    //                case _ => ExecutionConfiguration(executionMat._1).withSignalThreshold(0.01).withTimeLimit(executionMat._3)
    //              }
    //              evaluation = evaluation.addEvaluationRun(DcopMixedAlgorithmRun(optimizerPair._1, optimizerPair._2, proportion, evaluationGraph, executionConfig, runNumber, executionMat._2, getRevision, evalName).runAlgorithm)
    //            }
    //          }
    //        }
    //      }
    //    }
  }

  evaluation.execute

}