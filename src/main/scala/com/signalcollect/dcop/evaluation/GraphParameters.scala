package com.signalcollect.dcop.evaluation

import com.signalcollect.dcop.DcopAlgorithm
import com.signalcollect.Graph
import com.signalcollect.dcop.impl.RankedConfiguration
import com.signalcollect.dcop.modules.OptimizerModule
import com.signalcollect.GraphBuilder
import com.signalcollect.StateForwarderEdge
import com.signalcollect.dcop.graph.RankedVertexColoringEdge
import com.signalcollect.dcop.graph.SimpleDcopVertex
import com.signalcollect.dcop.graph.RankedDcopVertex
import scala.io.Source
import scala.annotation.tailrec

abstract class EvaluationGraphParameters {
  def initialValue: (Set[Int]) => Int
}

case class GridParameters(val domain: Set[Int], initValue: (Set[Int]) => Int, val debug: Boolean, val width: Int) extends EvaluationGraphParameters {
  def initialValue = initValue
}

case class AdoptGraphParameters(adoptFileName: String, initValue: (Set[Int]) => Int, val debug: Boolean) extends EvaluationGraphParameters {
  def initialValue = initValue
}
