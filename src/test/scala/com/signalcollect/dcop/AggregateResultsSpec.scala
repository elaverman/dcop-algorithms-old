/*
 *  @author Philip Stutz
 *  @author Mihaela Verman
 *  
 *  Copyright 2013 University of Zurich
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

package com.signalcollect.dcop

import scala.util.Random
import org.scalatest.FlatSpec
import org.scalatest.ShouldMatchers
import org.scalatest.prop.Checkers
import com.signalcollect._
import com.signalcollect.dcop.graph.SimpleDcopVertex
import org.scalacheck.Gen
import org.scalacheck.Arbitrary
import com.signalcollect.dcop.evaluation.AggregateResults
import com.signalcollect.dcop.evaluation.AggregateResultsFunctions

class AggregateResultsSpec extends FlatSpec with ShouldMatchers with Checkers with TestAnnouncements {

  var runId = 0

  "AggregateResults" should "correctly aggregate over one small file" in {
    check(
      {

        val numberOfRuns = 1

        val dataProportion: Double = 1.0

        val numberOfSteps = 2

        val numberOfVariables = 7

        val inputFilenamePrefix = "testProblem"

        val folderPath = "testOutput"

        val fileTypes = Array(
          "NoRankConflictDsaBVertexColoringChangeProbability0.5")

        val functions = new AggregateResultsFunctions(numberOfRuns, dataProportion, numberOfSteps, numberOfVariables, inputFilenamePrefix, folderPath, fileTypes)

        functions.writeAverageConflictsOverTime
        functions.writeAverageLocalMinimaOverTime
        functions.writeUtilityMeasures
        functions.writeNeMeasures

        //        assert(idStateMap(0) != idStateMap(2), "Vertex 0 and vertex 2 have a color collision.")
        true
      },
      minSuccessful(1))
  }

  "AggregateResults" should "correctly aggregate over two small files" in {
    check(
      {

        val numberOfRuns = 1

        val dataProportion: Double = 1.0

        val numberOfSteps = 2

        val numberOfVariables = 7

        val inputFilenamePrefix = "testProblem"

        val folderPath = "testOutput"

        val fileTypes = Array(
          "NoRankConflictDsaBVertexColoringChangeProbability0.5",
          "NoRankConflictDsaBVertexColoringChangeProbability0.6")

        val functions = new AggregateResultsFunctions(numberOfRuns, dataProportion, numberOfSteps, numberOfVariables, inputFilenamePrefix, folderPath, fileTypes)

        functions.writeAverageConflictsOverTime
        functions.writeAverageLocalMinimaOverTime
        functions.writeUtilityMeasures
        functions.writeNeMeasures

        //        assert(idStateMap(0) != idStateMap(2), "Vertex 0 and vertex 2 have a color collision.")
        //9.0 4.0 2.0 
//completePathConf testOutput/testProblem/conflictsNoRankConflictDsaBVertexColoringChangeProbability0.6SynchronousSome(100)3Run0.txt
//File count is: 1
//9.0 4.0 0.0 
//Loc minima
//File count is: 1
//0.0 2.0 4.0 
//File count is: 1
//0.0 2.0 7.0 
//avg global utility ratios, end utility ratios, isOptimal
//0.4444444444444445, -1.0, 0.7777777777777778, -1.0, 0.0, -1.0
//0.5185185185185185, -1.0, 1.0, -1.0, 1.0, -1.0
//Found NE, avg number of steps
//0.0, -1.0, 0.0, -1.0
//1.0, -1.0, 2.0, -1.0
        true
      },
      minSuccessful(1))
  }

    "AggregateResults" should "correctly aggregate over two small diff runs files" in {
    check(
      {

        val numberOfRuns = 2

        val dataProportion: Double = 1.0

        val numberOfSteps = 2

        val numberOfVariables = 7

        val inputFilenamePrefix = "testProblem"

        val folderPath = "testOutput"

        val fileTypes = Array(
          "NoRankConflictDsaBVertexColoringChangeProbability0.5")

        val functions = new AggregateResultsFunctions(numberOfRuns, dataProportion, numberOfSteps, numberOfVariables, inputFilenamePrefix, folderPath, fileTypes)

        functions.writeAverageConflictsOverTime
        functions.writeAverageLocalMinimaOverTime
        functions.writeUtilityMeasures
        functions.writeNeMeasures

        //        assert(idStateMap(0) != idStateMap(2), "Vertex 0 and vertex 2 have a color collision.")
//File count is: 2
//9.0 4.0 1.0 
//Loc minima
//File count is: 2
//0.0 2.0 5.5 
//avg global utility ratios, end utility ratios, isOptimal
//0.4814814814814815, 0.0027434842249657, 0.8888888888888888, 0.024691358024691367, 0.5, 0.5
//Found NE, avg number of steps
//0.5, 0.5, 2.0, -1.0
        true
      },
      minSuccessful(1))
  }
  
}



