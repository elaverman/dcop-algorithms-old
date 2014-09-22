package com.signalcollect.dcop.evaluation

import com.signalcollect.Graph
import com.signalcollect.GraphBuilder
import com.signalcollect.StateForwarderEdge
import com.signalcollect.dcop.graph._

import scala.io.Source
import scala.annotation.tailrec

abstract class EvaluationGraphParameters[Action] {
  def initialValue: (Set[Action]) => Action
}

case class GridParameters[Action](val domain: Set[Action], initValue: (Set[Action]) => Action, val debug: Boolean, val width: Int) extends EvaluationGraphParameters[Action] {
  def initialValue = initValue
}

case class AdoptGraphParameters[Action](adoptFileName: String, initValue: (Set[Action]) => Action, val debug: Boolean) extends EvaluationGraphParameters[Action] {
  def initialValue = initValue
}

case class DimacsGraphParameters[Action](dimacsFileName: String, val domain: Set[Action], initValue: (Set[Action]) => Action, val debug: Boolean) extends EvaluationGraphParameters[Action] {
  def initialValue = initValue
}
