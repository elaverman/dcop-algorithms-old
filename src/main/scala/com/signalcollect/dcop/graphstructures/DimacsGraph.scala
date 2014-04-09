package com.signalcollect.dcop.graphstructures

import com.signalcollect.dcop.DcopAlgorithm

case class DimacsGraph(optimizer: DcopAlgorithm[Int, Int], domain: Set[Int], dimacsFileName: String, initialValue: (Set[Int]) => Int, debug: Boolean) extends ConstraintEvaluationGraph(optimizer) {

  val constraintGraphData = getConstraintGraphData(DimacsParser.parse(new java.io.File("dimacsInput/" + dimacsFileName)))

  val constraintGraph =
    constraintGraphData.buildConstraintGraphFromData(optimizer, initialValue, debug)

  //TODO: v Ask Philip about this next function
  //Attention: ConstraintGraphData assumes vertex ids from 0, while the Dimacs format assumes them from 1
  def getConstraintGraphData(entities: Traversable[DimacsEntity]): ConstraintGraphData = {
    var constraintGraphData = ConstraintGraphData(Map(), Map())
    for (e <- entities) {
      e match {
        case metaData: MetaData =>
          for (i <- (0 until metaData.nodes)) {
            constraintGraphData = constraintGraphData.addPossibleValues(i, domain)
          }
        case edgeDescriptor: EdgeDescriptor =>
          constraintGraphData = constraintGraphData.addConstraint((edgeDescriptor.source - 1, edgeDescriptor.target - 1))
        case other =>
      }
    }
    constraintGraphData
  }

  def graph = constraintGraph

  def computeNeighbours(id: Int) = constraintGraphData.neighbours.getOrElse(id, List())

  def size = constraintGraphData.neighbours.size

  def maxUtility = constraintGraphData.neighbours.map(x => x._2.size).sum

  def domainForVertex(id: Int) = constraintGraphData.possibleValues.getOrElse(id, Set())

  override def toString = dimacsFileName

}