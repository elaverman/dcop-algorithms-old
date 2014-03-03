package com.signalcollect.dcop.graphstructures

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import com.signalcollect.dcop.NoRankConflictDsaBVertexColoring

class DimacsGraphSpec extends FlatSpec with Matchers {

  def zeroInitialized(domain: Set[Int]) = 0

  "DimacsGraph" should "correctly get the structure from the file" in {
    val dimacsGraph = DimacsGraph(NoRankConflictDsaBVertexColoring(changeProbability = 0.5), (0 to 1).toSet, "dimacsGraphSpecInput.col", zeroInitialized, false)
    assert(dimacsGraph.domain == (0 to 1).toSet)
  }

}
