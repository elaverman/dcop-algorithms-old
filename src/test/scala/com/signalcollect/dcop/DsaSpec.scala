/*
 *  @author Philip Stutz
 *  @author Mihaela Verman
 *  
 *  Copyright 2013 University of Zurich
 *      
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *         http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 */

package com.signalcollect.dcop

import scala.util.Random
import org.scalatest.FlatSpec
import org.scalatest.ShouldMatchers
import org.scalatest.prop.Checkers
import com.signalcollect._
import com.signalcollect.dcop.graph.SimpleDcopVertex

class DsaSpec extends FlatSpec with ShouldMatchers with Checkers {

  "DsaA" should "correctly assign colors to a small test graph" in {
    check(
      (irrelevantParameter: Int) => {
        val g = GraphBuilder.build
        val optimizer = DsaVertexColoring(changeProbability = 1.0)
        val domain = (0 to 1).toSet
        def randomFromDomain = domain.toSeq(Random.nextInt(domain.size))
        val v0 = new SimpleDcopVertex(0, domain, optimizer, randomFromDomain, true)
        val v1 = new SimpleDcopVertex(1, domain, optimizer, randomFromDomain, true)
        val v2 = new SimpleDcopVertex(2, domain, optimizer, randomFromDomain, true)
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



