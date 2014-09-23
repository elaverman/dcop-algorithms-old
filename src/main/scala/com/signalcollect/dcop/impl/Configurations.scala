package com.signalcollect.dcop.impl

import com.signalcollect.dcop.modules._

case class SimpleConfig[Id, Action](
  val neighborhood: Map[Id, Action],
  val domain: Set[Action],
  val centralVariableAssignment: (Id, Action)) extends Configuration[Id, Action] {
  def withCentralVariableAssignment(value: Action) = {
    this.copy(centralVariableAssignment = (centralVariableAssignment._1, value)).asInstanceOf[this.type]
  }

  def computeExpectedNumberOfConflicts = {
    val occupiedColors = neighborhood.values
    val numberOfConflicts = occupiedColors.filter(_ == centralVariableValue).size
    numberOfConflicts
  }

  override def toString = s"      neighborhood = $neighborhood.toString\n" +
    s"      domain = $domain.toString\n" +
    s"      centralVariableAssignment = $centralVariableAssignment.toString\n"
}

case class RankedConfig[Id, Action](
  val neighborhood: Map[Id, Action],
  val ranks: Map[Id, Double],
  val domain: Set[Action],
  val centralVariableAssignment: (Id, Action)) extends RankedConfiguration[Id, Action] {

  def withCentralVariableAssignment(value: Action): this.type = {
    this.copy(centralVariableAssignment = (centralVariableAssignment._1, value)).asInstanceOf[this.type]
  }

  def computeExpectedNumberOfConflicts = {
    val occupiedColors = neighborhood.values
    val numberOfConflicts = occupiedColors.filter(_ == centralVariableValue).size
    numberOfConflicts
  }

  override def toString = s"      neighborhood = $neighborhood.toString\n" +
    s"      ranks = $ranks.toString\n" +
    s"      domain = $domain.toString\n" +
    s"      centralVariableAssignment = $centralVariableAssignment.toString\n"

}

