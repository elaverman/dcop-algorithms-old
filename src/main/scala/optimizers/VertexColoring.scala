package optimizers

import com.signalcollect.DataGraphVertex
import com.signalcollect._
import scala.util.Random

class VertexColoringVertex(
  override val id: Int,
  val domain: Set[Int],
  val optimizer: DecisionRule[Int, Int],
  initialState: Int) extends DataGraphVertex(id, initialState) {

  type Signal = Int

  def collect = {
    val neighborhood: Map[Int, Int] = mostRecentSignalMap.seq.toMap.asInstanceOf[Map[Int, Int]]
    val centralVariableAssignment = (id, state)
    val currentConfig = VertexColoringConfig(neighborhood, domain, centralVariableAssignment)
    val newState = optimizer.chooseNewAssignment(currentConfig)
    newState
  }
}

//object DsaATest extends App {
//  val g = GraphBuilder.build
//  val optimizer = VertexColoringDsaA(changeThreshold = 0.5)
//  val domain = (0 to 1).toSet
//  def randomFromDomain = domain.toSeq(Random.nextInt(domain.size))
//  val v0 = new VertexColoringVertex(0, domain, optimizer, randomFromDomain)
//  val v1 = new VertexColoringVertex(1, domain, optimizer, randomFromDomain)
//  val v2 = new VertexColoringVertex(2, domain, optimizer, randomFromDomain)
//  g.addVertex(v0)
//  g.addVertex(v1)
//  g.addVertex(v2)
//  
//}
