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
import com.signalcollect.dcop.modules.OptimizerModule
import org.scalacheck.Gen
import org.scalacheck.Arbitrary
import com.signalcollect.dcop.evaluation.AggregateResults
import com.signalcollect.dcop.evaluation.AggregateResultsFunctions
import com.signalcollect.dcop.evaluation._
import com.signalcollect.dcop.graphstructures.AdoptGraph

class PrintersSpec extends FlatSpec with ShouldMatchers with Checkers with TestAnnouncements {

  "countConflicts" should "correctly aggregate over a map" in {
    check(
      {
        val aggregate: Map[Int, (Int, Double)] = Map((0 to 39) map { x => (x -> (0, 0.15)) }: _*)
        val aGraph = AdoptGraph(NoRankConflictDsaBVertexColoring(changeProbability = 0.4), "Problem-GraphColor-40_3_2_0.4_r0", initialValue = x => 0, false)
        assert(ColorPrinter[(Int,Double)](aGraph).countConflicts(aggregate) == 80, "Did not calculate number of conflicts correctly.")
        aGraph.graph.shutdown
        true
      },
      minSuccessful(1))
  }

}



