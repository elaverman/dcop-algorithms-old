package com.signalcollect.dcop.evaluation

import com.signalcollect.Graph
import com.signalcollect.GraphBuilder
import com.signalcollect.StateForwarderEdge
import com.signalcollect.dcop.graph._

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

case class DimacsGraphParameters(dimacsFileName: String, val domain: Set[Int], initValue: (Set[Int]) => Int, val debug: Boolean) extends EvaluationGraphParameters {
  def initialValue = initValue
}
