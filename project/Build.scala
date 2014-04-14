import sbt._
import Keys._

object OptimizersBuild extends Build {
   val scCore = ProjectRef(file("../signal-collect-slurm"), id = "signal-collect-slurm")
   val scOptimizers = Project(id = "optimizers", base = file(".")) dependsOn(scCore)
}
