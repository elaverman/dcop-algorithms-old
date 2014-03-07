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
    localJarPath = assemblyPath, jvmParameters = jvmParameters, jdkBinPath = "/home/user/verman/jdk1.7.0_45/bin/", priority = TorquePriority.slow)
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
  def evalName = s"Dimacsflat1000_76-rep"
  def runs = 1
  var evaluation = new Evaluation(evaluationName = evalName, executionHost = kraken).addResultHandler(mySql)
  //    var evaluation = new Evaluation(evaluationName = evalName, executionHost = localHost).addResultHandler(mySql)
  /*********/

  val optimizers: List[DcopAlgorithm[Int, Int]] = List(
    //    ConflictDsaBVertexColoring(changeProbability = 0.9),
    //        ConflictDsaBVertexColoring(changeProbability = 0.8),
    //    ConflictDsaBVertexColoring(changeProbability = 0.5),
    //    RankedDsaBVertexColoring(changeProbability = 0.9),
    //    RankedDsaBVertexColoring(changeProbability = 0.7),
    //    RankedDsaBVertexColoring(changeProbability = 1.0)
    //    NoRankConflictDsaBVertexColoring(changeProbability = 0.7),
    //    RankedConflictDsaBVertexColoring(changeProbability = 0.7),
    //    NoRankConflictExplorerDsaBVertexColoring(changeProbability = 0.9, explore = 0.01), //NoRankConflictExplorerDsaBVertexColoring(changeProbability = 0.7, explore = 0.05) 
    //    RankedConflictExplorerDsaBVertexColoring(changeProbability = 0.9, explore = 0.01),
    //    RankedConflictExplorerDsaBVertexColoring(changeProbability = 0.9, explore = 0.1),
    //    RankedConflictExplorerDsaBVertexColoring(changeProbability = 0.9, explore = 0.3),
    //new for Ranked inertia
    //    NoRankConflictDsaBVertexColoring(changeProbability = 0.7),
    //    RankedConflictDsaBVertexColoring(changeProbability = 0.7),
    //    NoRankConflictDsaBVertexColoring(changeProbability = 0.5),
    NoRankConflictDsaBVertexColoring(changeProbability = 0.6),
    //    NoRankConflictDsaBVertexColoring(changeProbability = 0.7),
    //    NoRankConflictDsaBVertexColoring(changeProbability = 0.8),
    //    NoRankConflictDsaBVertexColoring(changeProbability = 0.9),
    //   RankedConflictDsaBVertexColoring(changeProbability = 0.8),
    //    NoRankConflictDsaBVertexColoring(changeProbability = 0.9),
    //    RankedConflictDsaBVertexColoring(changeProbability = 0.9),
    //  NoRankConflictDsaBVertexColoring(changeProbability = 0.95),
    //  RankedConflictDsaBVertexColoring(changeProbability = 0.95),
    //new Ranked inertia  
    //    NoRankConflictDsaBVertexColoringWithRankedChangeProbability(relativeChangeProbability = 0.7),
    //    RankedConflictDsaBVertexColoringWithRankedChangeProbability(relativeChangeProbability = 0.7),
    //  NoRankConflictDsaBVertexColoringWithRankedChangeProbability(relativeChangeProbability = 0.8),
    // RankedConflictDsaBVertexColoringWithRankedChangeProbability(relativeChangeProbability = 0.8),
    //    NoRankConflictDsaBVertexColoringWithRankedChangeProbability(relativeChangeProbability = 0.9),
    //    RankedConflictDsaBVertexColoringWithRankedChangeProbability(relativeChangeProbability = 0.9),
    //  NoRankConflictDsaBVertexColoringWithRankedChangeProbability(relativeChangeProbability = 0.97),
    //  RankedConflictDsaBVertexColoringWithRankedChangeProbability(relativeChangeProbability = 0.97),
    //new: Inverted Ranks inertia
    //        NoRankConflictDsaBVertexColoringWithInvertedRankedChangeProbability(relativeChangeProbability = 0.7),
    //  NoRankConflictDsaBVertexColoringWithInvertedRankedChangeProbability(relativeChangeProbability = 0.8),
    //        NoRankConflictDsaBVertexColoringWithInvertedRankedChangeProbability(relativeChangeProbability = 0.9),
    //   NoRankConflictDsaBVertexColoringWithInvertedRankedChangeProbability(relativeChangeProbability = 0.97),
    //    RankedConflictDsaBVertexColoringWithInvertedRankedChangeProbability(relativeChangeProbability = 0.7),
    //   RankedConflictDsaBVertexColoringWithInvertedRankedChangeProbability(relativeChangeProbability = 0.8),
    //    RankedConflictDsaBVertexColoringWithInvertedRankedChangeProbability(relativeChangeProbability = 0.9)
    //  RankedConflictDsaBVertexColoringWithInvertedRankedChangeProbability(relativeChangeProbability = 0.97))
    //      NoRankConflictDsaBVertexColoringWithDynamicRankedChangeProbability(relativeChangeProbability = 0.8),
    // NoRankConflictDsaBVertexColoringWithDynamicRankedChangeProbability(relativeChangeProbability = 0.97),
    //    DynamicRankedConflictDsaBVertexColoring(changeProbability = 0.5),
    DynamicRankedConflictDsaBVertexColoring(changeProbability = 0.6))
  //    DynamicRankedConflictDsaBVertexColoring(changeProbability = 0.7),
  //    DynamicRankedConflictDsaBVertexColoring(changeProbability = 0.8),
  //    DynamicRankedConflictDsaBVertexColoring(changeProbability = 0.9))
  //   DynamicRankedConflictDsaBVertexColoring(changeProbability = 0.7),
  //   DynamicRankedConflictDsaBVertexColoring(changeProbability = 0.95))

  val execModesAggrIntervAndTermLimits = List(
    //   (ExecutionMode.PureAsynchronous, 30000, 3600000), //100, 100000) //420000L)
    (ExecutionMode.Synchronous, 1, 500) //30, 3600) //5, 800),
    )

  //TODO: 2 lists of settings for grids and adopt etc. & combine them with optimizers 
  //  case class AdoptGraph(optimizer: DcopAlgorithm[Int, Int], adoptFileName: String, initialValue: (Set[Int]) => Int, debug: Boolean)
  //  case class Grid(optimizer: DcopAlgorithm[Int, Int], domain: Set[Int], initialValue: (Set[Int]) => Int, debug: Boolean, width: Int)

  val adoptGraphNamesList = new java.io.File("adoptInput").listFiles.filter(x => (x.getName.startsWith("Problem-GraphColor-40"))).map(_.getName)
  val dimacsGraphNamesList = new java.io.File("dimacsInput").listFiles.filter(x => (x.getName.endsWith("flat1000_76_0.col"))).map(_.getName)

  for (runNumber <- (0 until runs)) {
    for (optimizer <- optimizers) {
      val evaluationGraphs = //List(GridParameters((0 to 3).toSet, zeroInitialized, debug, 8)) // ++
        // adoptGraphNamesList.map(x => AdoptGraphParameters(x, zeroInitialized, debug)) // ++
        dimacsGraphNamesList.map(x => DimacsGraphParameters(x, (0 to 3).toSet, zeroInitialized, debug)) ++
          dimacsGraphNamesList.map(x => DimacsGraphParameters(x, (0 to 75).toSet, zeroInitialized, debug)) ++
          dimacsGraphNamesList.map(x => DimacsGraphParameters(x, (0 to 99).toSet, zeroInitialized, debug))
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
  evaluation.execute

}