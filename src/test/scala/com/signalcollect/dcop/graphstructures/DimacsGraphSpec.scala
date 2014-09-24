//package com.signalcollect.dcop.graphstructures
//
//import org.scalatest.FlatSpec
//import org.scalatest.Matchers
////import com.signalcollect.dcop.NoRankConflictDsaBVertexColoring
//import com.signalcollect.dcop.TestAnnouncements
//import com.signalcollect.dcop.DsaAVertexColoring
//import com.signalcollect.dcop.graphstructures.DimacsGraph
//
//class DimacsGraphSpec extends FlatSpec with Matchers with TestAnnouncements {
//
//  def zeroInitialized(domain: Set[Int]) = 0
//
//  "DimacsGraph" should "correctly get the structure from the file" in {
//    val dimacsGraph = DimacsGraph(DsaAVertexColoring[Int, Int](changeProbability = 0.5), (0 to 1).toSet, "dimacsGraphSpecInput.col", zeroInitialized, false)
//    assert(dimacsGraph.domain == (0 to 1).toSet)
//    dimacsGraph.graph.shutdown
//  }
//
//}
