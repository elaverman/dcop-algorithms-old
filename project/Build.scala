import sbt._
import Keys._

object OptimizersBuild extends Build {
   val scCore = ProjectRef(file("../signal-collect-torque"), id = "signal-collect-torque")
   val scOptimizers = Project(id = "optimizers", base = file(".")) dependsOn(scCore)
}
