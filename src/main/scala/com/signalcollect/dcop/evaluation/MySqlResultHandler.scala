/*
 *  @author Daniel Strebel
 *  @author Philip Stutz
 *  
 *  Copyright 2012 University of Zurich
 *      
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *         http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 */

package com.signalcollect.dcop.evaluation

import java.net.URL
import scala.collection.JavaConversions._
import scala.slick.driver.MySQLDriver.simple._
import com.signalcollect.nodeprovisioning.torque._
import java.net.InetAddress

class AllResults(tag: Tag) extends Table[RowType](tag, "AllResults") {
  // def result_id = column[Int]("result_id", O.PrimaryKey, O.AutoInc, O.NotNull) // INT NOT NULL AUTO_INCREMENT,
  def evaluationDescription = column[String]("evaluationDescription")
  def optimizer = column[String]("optimizer")
  def utility = column[Double]("utility")
  def domainSize = column[Int]("domainSize")
  def graphSize = column[Int]("graphSize")
  def executionMode = column[String]("executionMode")
  def conflictCount = column[Int]("conflictCount")
  def avgGlobalUtilityRatio = column[Double]("avgGlobalUtilityRatio")
  def endUtilityRatio = column[Double]("endUtilityRatio")
  def isOptimal = column[Int]("isOptimal")
  def timeToFirstLocOptimum = column[Int]("timeToFirstLocOptimum")
  def messagesPerVertexPerStep = column[Double]("messagesPerVertexPerStep")
  //isOptimizerRanked VARCHAR(10),
  def revision = column[String]("revision")
  def aggregationInterval = column[Int]("aggregationInterval")
  def run = column[Int]("run")
  def stepsLimit = column[String]("stepsLimit")
  //timeLimit VARCHAR(20),
  def graphStructure = column[String]("graphStructure")
  def jobId = column[String]("jobId")//, O.PrimaryKey)
  def computationTimeInMilliseconds = column[Double]("computationTimeInMilliseconds")
  def date = column[String]("date")
  def executionHostname = column[String]("executionHostname")

  def * = (evaluationDescription, optimizer, utility, domainSize, graphSize, executionMode, conflictCount,
    avgGlobalUtilityRatio, endUtilityRatio, isOptimal, timeToFirstLocOptimum, messagesPerVertexPerStep,
    revision, aggregationInterval, run, stepsLimit, graphStructure, jobId, computationTimeInMilliseconds,
    date, executionHostname)
    
  def pk = primaryKey("pk_composite", (evaluationDescription, jobId))

}

class MySqlResultHandler(username: String, password: String, ipAddress: String)
  extends Function1[Map[String, String], Unit]
  with Serializable {

  def allResults = TableQuery[AllResults] //if used with def it will never be serialized

  def apply(data: Map[String, String]) = {

    //TODO move from data to tuple
    val dataEvaluationDescription = data.getOrElse("evaluationDescription", "")
    val dataOptimizer = data.getOrElse("optimizer", "")
    val dataUtility = data.getOrElse("utility", "").toDouble
    val dataDomainSize = data.getOrElse("domainSize", "").toInt
    val dataGraphSize = data.getOrElse("graphSize", "").toInt
    val dataExecutionMode = data.getOrElse("executionMode", "")
    val dataConflictCount = data.getOrElse("conflictCount", "").toInt
    val dataAvgGlobalUtilityRatio = data.getOrElse("avgGlobalUtilityRatio", "").toDouble // Measure (1)
    val dataEndUtilityRatio = data.getOrElse("endUtilityRatio", "").toDouble // Measure (2)
    val dataIsOptimal = data.getOrElse("isOptimal", "").toInt // Measure (3)
    val dataTimeToFirstLocOptimum = data.getOrElse("timeToFirstLocOptimum", "").toInt // Measure (4)
    val dataMessagesPerVertexPerStep = data.getOrElse("messagesPerVertexPerStep", "").toDouble // Measure (5)
    val dataIsOptimizerRanked = data.getOrElse("isOptimizerRanked", "")
    val dataRevision = data.getOrElse("revision", "")
    val dataAggregationInterval = data.getOrElse("aggregationInterval", "").toInt
    val dataRun = data.getOrElse("run", "").toInt
    val dataStepsLimit = data.getOrElse("stepsLimit", "")
    //val dataTimeLimit = data.getOrElse("timeLimit", "")
    val dataGraphStructure = data.getOrElse("graphStructure", "")
    val dataJobId = data.getOrElse("jobId", "")
    val dataComputationTimeInMilliseconds = data.getOrElse("computationTimeInMilliseconds", "").toDouble
    val dataDate = data.getOrElse("date", "")
    val dataExecutionHostName = data.getOrElse("executionHostname", "")

    //    val dataSignalThreshold = data.getOrElse("signalThreshold", "").toDouble
    //    val dataCollectThreshold = data.getOrElse("collectThreshold", "").toDouble

    val runResultMySql = (dataEvaluationDescription, dataOptimizer, dataUtility, dataDomainSize,
      dataGraphSize, dataExecutionMode, dataConflictCount,
      dataAvgGlobalUtilityRatio, dataEndUtilityRatio, dataIsOptimal, dataTimeToFirstLocOptimum, dataMessagesPerVertexPerStep,
      //dataIsOptimizerRanked,
      dataRevision, dataAggregationInterval, dataRun, dataStepsLimit, //dataTimeLimit,
      dataGraphStructure, dataJobId, dataComputationTimeInMilliseconds,
      dataDate, dataExecutionHostName)

    //    val address = InetAddress.getByName(ipAddress)
    //    val realIp = address.getHostAddress
    //    
    //    println(s"Ip address for $ipAddress is $realIp")

    Database.forURL(s"jdbc:mysql://$ipAddress/optimizers_db", user = username, password = password, driver = "com.mysql.jdbc.Driver") withSession {
      implicit session =>
//         allResults.ddl.create
        actionWithExponentialRetry(() => allResults += runResultMySql)
    }

  }

  def actionWithExponentialRetry[G](action: () => G): G = {
    try {
      action()
    } catch {
      case e: Exception =>
        // just retry a few times
        try {
          println("Database API exception: " + e)
          println("Database API retry in 1 second")
          Thread.sleep(1000)
          println("Retrying.")
          action()
        } catch {
          case e: Exception =>
            try {
              println("Database API exception: " + e)
              println("Database API retry in 10 seconds")
              Thread.sleep(10000)
              println("Retrying.")
              action()
            } catch {
              case e: Exception =>
                try {
                  println("Database API exception: " + e)
                  println("Database API retry in 100 seconds")
                  Thread.sleep(100000)
                  println("Retrying.")
                  action()
                } catch {
                  case e: Exception =>
                    println("Database did not acknowledge write: " + e)
                    null.asInstanceOf[G]
                }
            }
        }
    }
  }

}