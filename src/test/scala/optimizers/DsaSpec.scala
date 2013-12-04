package optimizers

import org.scalatest.FlatSpec
import org.scalatest.ShouldMatchers
import org.scalatest.prop.Checkers
import com.signalcollect._
import scala.util.Random
import org.scalacheck.Gen
import org.scalacheck.Gen._
import org.scalacheck.Arbitrary

class DsaSpec extends FlatSpec with ShouldMatchers with Checkers {

  "DsaA" should "correctly assign colors to a small test graph" in {
    check(
      (irrelevantParameter: Int) => {
        val g = GraphBuilder.build
        val optimizer = VertexColoringDsaA(changeThreshold = 1.0)
        val domain = (0 to 1).toSet
        def randomFromDomain = domain.toSeq(Random.nextInt(domain.size))
        val v0 = new VertexColoringVertex(0, domain, optimizer, randomFromDomain)
        val v1 = new VertexColoringVertex(1, domain, optimizer, randomFromDomain)
        val v2 = new VertexColoringVertex(2, domain, optimizer, randomFromDomain)
        g.addVertex(v0)
        g.addVertex(v1)
        g.addVertex(v2)
        g.addEdge(v0.id, new StateForwarderEdge(v1.id))
        g.addEdge(v1.id, new StateForwarderEdge(v0.id))
        g.addEdge(v0.id, new StateForwarderEdge(v2.id))
        g.addEdge(v2.id, new StateForwarderEdge(v0.id))
        g.execute
        val idStateMap = g.aggregate[Map[Int, Int]](new IdStateMapAggregator[Int, Int])
        assert(idStateMap(0) != idStateMap(1), "Vertex 0 and vertex 1 have a color collision.")
        assert(idStateMap(0) != idStateMap(2), "Vertex 0 and vertex 2 have a color collision.")
        true
      },
      minSuccessful(20))
  }
}

