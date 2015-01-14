package com.signalcollect.dcop.impl

import com.signalcollect.dcop.modules._

case class SimpleConfig[Id, Action](
  val neighborhood: Map[Id, Action],
  val numberOfCollects: Long,
  val domain: Set[Action],
  val centralVariableAssignment: (Id, Action)) extends Configuration[Id, Action, Action] {
  final def withCentralVariableAssignment(value: Action) = {
    this.copy(centralVariableAssignment = (centralVariableAssignment._1, value)).asInstanceOf[this.type]
  }

  //TODO: Used for ArgmaxB decision rule and for ZeroConflictConvergenceDetection.
  def computeExpectedNumberOfConflicts = {
    val occupiedColors = neighborhood.values
    val numberOfConflicts = occupiedColors.filter(_ == centralVariableValue).size
    numberOfConflicts
  }

  /**
   *  For convenience, these are usable as stub implementations.
   */
  def isSameState(other: this.type): Boolean = ???
  def updateNeighborhood(neighborhood: Map[Id, Action]): this.type = ???

  override def toString = s"      neighborhood = $neighborhood.toString\n" +
    s"      domain = $domain.toString\n" +
    s"      centralVariableAssignment = $centralVariableAssignment.toString\n"
}

class MemoryConfig[Id, Action, SignalType, UtilityType](
  val neighborhood: Map[Id, Action],
  val memory: Map[Action, UtilityType],
  val numberOfCollects: Long,
  val domain: Set[Action],
  val centralVariableAssignment: (Id, Action)) extends Configuration[Id, Action, SignalType] {

  final def withCentralVariableAssignment(value: Action) = {
    this.copy(centralVariableAssignment = (centralVariableAssignment._1, value)).asInstanceOf[this.type]
  }

  //TODO: Used for ArgmaxB decision rule and for ZeroConflictConvergenceDetection.
  def computeExpectedNumberOfConflicts = {
    val occupiedColors = neighborhood.values
    val numberOfConflicts = occupiedColors.filter(_ == centralVariableValue).size
    numberOfConflicts
  }

  /**
   *  For convenience, these are usable as stub implementations.
   */
  def isSameState(other: this.type): Boolean = ???
  def updateNeighborhood(neighborhood: Map[Id, Action]): this.type = ???

  override def toString = s"      neighborhood = $neighborhood.toString\n" +
    s"      domain = $domain.toString\n" +
    s"      centralVariableAssignment = $centralVariableAssignment.toString\n"
}

case class SimpleMemoryConfig[Id, Action, UtilityType](
  val neighborhood: Map[Id, Action],
  val memory: Map[Action, UtilityType],
  val numberOfCollects: Long,
  val domain: Set[Action],
  val centralVariableAssignment: (Id, Action)) extends Configuration[Id, Action, Action] {

  final def withCentralVariableAssignment(value: Action) = {
    this.copy(centralVariableAssignment = (centralVariableAssignment._1, value)).asInstanceOf[this.type]
  }

  def computeExpectedNumberOfConflicts = {
    val occupiedColors = neighborhood.values
    val numberOfConflicts = occupiedColors.filter(_ == centralVariableValue).size
    numberOfConflicts
  }

  /**
   *  For convenience, these are usable as stub implementations.
   */
  def isSameState(other: this.type): Boolean = ???
  def updateNeighborhood(neighborhood: Map[Id, Action]): this.type = ???

  override def toString = s"\n      neighborhood = $neighborhood.toString\n" +
    s"      domain = $domain.toString\n" +
    s"      centralVariableAssignment = $centralVariableAssignment.toString\n" +
    s"      memory = $memory\n"
}

case class RankedConfig[Id, Action](
  val neighborhood: Map[Id, Action],
  val numberOfCollects: Long,
  val ranks: Map[Id, Double],
  val domain: Set[Action],
  val centralVariableAssignment: (Id, Action)) extends Configuration[Id, Action, (Action, Double)] {

  /*
   * Not only the value changes, but also the rank.  
   */
  final def withCentralVariableAssignment(value: Action): this.type = {
    this.copy(centralVariableAssignment = (centralVariableAssignment._1, value)).asInstanceOf[this.type]
  }

  def computeExpectedNumberOfConflicts = {
    val occupiedColors = neighborhood.values
    val numberOfConflicts = occupiedColors.filter(_ == centralVariableValue).size
    numberOfConflicts
  }

  /**
   *  For convenience, these are usable as stub implementations.
   */
  def isSameState(other: this.type): Boolean = ???
  def updateNeighborhood(neighborhood: Map[Id, (Action, Double)]): this.type = ???

  override def toString = s"      neighborhood = $neighborhood.toString\n" +
    s"      ranks = $ranks.toString\n" +
    s"      domain = $domain.toString\n" +
    s"      centralVariableAssignment = $centralVariableAssignment.toString\n"

}

