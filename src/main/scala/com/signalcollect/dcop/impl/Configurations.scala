package com.signalcollect.dcop.impl

import com.signalcollect.dcop.modules._

trait SimpleConfiguration[Id, Action, ConstraintParams] extends ConfigurationModule[Id, Action, ConstraintParams] {
  this: OptimizerModule[Id, Action, ConstraintParams] =>

  type Config = SimpleConfig
  type Factory = SimpleConfigFactory

  val factory = new Factory

  class SimpleConfigFactory extends ConfigFactory {

    /**
     * Not typesafe, accepts whatever is passed and crashes at runtime if the
     * parameters are wrong. :(
     */
    def createNewConfig(params: Any*): Config = {
      val neighborhood = params(0).asInstanceOf[Map[Id, Action]]
      val domain = params(1).asInstanceOf[Set[Action]]
      val centralVariableAssignment = params(2).asInstanceOf[(Id, Action)]
      SimpleConfig(neighborhood, domain, centralVariableAssignment)
    }
  }

  case class SimpleConfig(
    val neighborhood: Map[Id, Action],
    val domain: Set[Action],
    val centralVariableAssignment: (Id, Action)) extends Configuration {
    def withCentralVariableAssignment(value: Action): Config = {
      this.copy(centralVariableAssignment = (centralVariableAssignment._1, value))
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

}

class RankedConfiguration[Id, Action, ConstraintParams] extends ConfigurationModule[Id, Action, ConstraintParams] {
  this: OptimizerModule[Id, Action, ConstraintParams] =>

  type Config = RankedConfig
  type Factory = RankedConfigFactory

  val factory = new Factory

  class RankedConfigFactory extends ConfigFactory {

    /**
     * Not typesafe, accepts whatever is passed and crashes at runtime if the
     * parameters are wrong. :(
     *
     * ranks has to include the central variable rank.
     *
     * TODO: Assess if centralVariableRank could be removed from ranks.
     * TODO: Should reevaluate currentConfig function on RankedConfigCreation.
     */
    def createNewConfig(params: Any*): Config = {
      val neighborhood = params(0).asInstanceOf[Map[Id, Action]]
      val ranks = params(1).asInstanceOf[Map[Id, Double]]
      val domain = params(2).asInstanceOf[Set[Action]]
      val centralVariableAssignment = params(3).asInstanceOf[(Id, Action)]
      RankedConfig(neighborhood, ranks, domain, centralVariableAssignment)
    }
  }

  case class RankedConfig(
    val neighborhood: Map[Id, Action],
    val ranks: Map[Id, Double],
    val domain: Set[Action],
    val centralVariableAssignment: (Id, Action)) extends Configuration {
    def withCentralVariableAssignment(value: Action): Config = {
      this.copy(centralVariableAssignment = (centralVariableAssignment._1, value))
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

}