import sbt._
import Keys._

object OptimizersBuild extends Build {
   val scCore = ProjectRef(file("../signal-collect"), id = "signal-collect")
   val scOptimizers = Project(id = "optimizers", base = file(".")) dependsOn(scCore)
}
