///*
// *  @author Philip Stutz
// *  @author Mihaela Verman
// *  
// *  Copyright 2013 University of Zurich
// *      
// *  Licensed under the Apache License, Version 2.0 (the "License");
// *  you may not use this file except in compliance with the License.
// *  You may obtain a copy of the License at
// *  
// *         http://www.apache.org/licenses/LICENSE-2.0
// *  
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// *  
// */
//
//package com.signalcollect.dcop
//
//import scala.util.Random
//import org.scalatest.FlatSpec
//import org.scalatest.ShouldMatchers
//import org.scalatest.prop.Checkers
//import com.signalcollect._
//import com.signalcollect.dcop.graph.SimpleDcopVertex
//import org.scalacheck.Gen
//import org.scalacheck.Arbitrary
//import com.signalcollect.dcop.evaluation.AggregateResults
//import com.signalcollect.dcop.evaluation.AggregateResultsFunctions
//import com.signalcollect.dcop.evaluation._
//
//class ModulesSpec extends FlatSpec with ShouldMatchers with Checkers with TestAnnouncements {
//
//  "A 2-Vertex graph" should "correctly assign 2-colors" in {
//    check(
//      {
//        def initial0Value = 0
//        val g = GraphBuilder.build
//        val vertex0 = new SimpleDcopVertex(0, Set(0,1), new SimpleOptimizer[Int, Int], initial0Value, debug = true)
//        val vertex1 = new SimpleDcopVertex(0, Set(0,1), new SimpleOptimizer[Int, Int], initial0Value, debug = true)
//        g.addVertex(vertex0)
//        g.addVertex(vertex1)
//        assert(vertex0.state != vertex1.state)
//        g.shutdown
//        true
//      },
//      minSuccessful(100))
//  }
//
//}
//
//
//
