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
import com.signalcollect.dcop.modules.OptimizerModule
import org.scalacheck.Gen
import org.scalacheck.Arbitrary

class DsaSpec extends FlatSpec with ShouldMatchers with Checkers {

  lazy val smallWidth = Gen.chooseNum(1, 10).map(Width(_))
  implicit def arbSmallWidth[Width] = Arbitrary(smallWidth)

  // Returns all the neighboring cells of the cell with the given row/column
  def potentialNeighbours(column: Int, row: Int): List[(Int, Int)] = {
    List(
      (column - 1, row - 1), (column, row - 1), (column + 1, row - 1),
      (column - 1, row), (column + 1, row),
      (column - 1, row + 1), (column, row + 1), (column + 1, row + 1))
  }

  // Tests if a cell is within the grid boundaries
  def inGrid(column: Int, row: Int, width: Int): Boolean = {
    column >= 0 && row >= 0 && column < width && row < width
  }

  def neighbours(id: Int, width: Int): Iterable[Int] = {

    val column: Int = id % width
    val row: Int = id / width

    potentialNeighbours(column, row).filter(coordinate => inGrid(coordinate._1, coordinate._2, width)) map
      (coordinate => (coordinate._2 * width + coordinate._1))
  }
  var runId = 0

  "DsaA" should "correctly assign colors to a small test graph" in {
    check(
      (irrelevantParameter: Int) => {
        val g = GraphBuilder.build
        val optimizer = DsaAVertexColoring(changeProbability = 1.0)
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
      minSuccessful(1))
  }

  "RankedDsaA" should "correctly assign colors to a 2x2 grid" in {
    check(
      (irrelevantParameter: Int) => {
        runId += 1
        try {
          println(s"STARTING TEST RUN $runId")
          val g = GraphBuilder.build
          val optimizer = DsaAVertexColoring(changeProbability = 1.0)
          val domain = (0 until 4).toSet
          def randomFromDomain = domain.toSeq(Random.nextInt(domain.size))
          val debug = false
          val v0 = new SimpleDcopVertex(0, domain, optimizer, randomFromDomain, debug = debug)
          val v1 = new SimpleDcopVertex(1, domain, optimizer, randomFromDomain, debug = debug)
          val v2 = new SimpleDcopVertex(2, domain, optimizer, randomFromDomain, debug = debug)
          val v3 = new SimpleDcopVertex(3, domain, optimizer, randomFromDomain, debug = debug)
          g.addVertex(v0)
          g.addVertex(v1)
          g.addVertex(v2)
          g.addVertex(v3)
          g.addEdge(v0.id, new StateForwarderEdge(v1.id))
          g.addEdge(v0.id, new StateForwarderEdge(v2.id))
          g.addEdge(v0.id, new StateForwarderEdge(v3.id))
          g.addEdge(v1.id, new StateForwarderEdge(v0.id))
          g.addEdge(v1.id, new StateForwarderEdge(v2.id))
          g.addEdge(v1.id, new StateForwarderEdge(v3.id))
          g.addEdge(v2.id, new StateForwarderEdge(v0.id))
          g.addEdge(v2.id, new StateForwarderEdge(v1.id))
          g.addEdge(v2.id, new StateForwarderEdge(v3.id))
          g.addEdge(v3.id, new StateForwarderEdge(v0.id))
          g.addEdge(v3.id, new StateForwarderEdge(v1.id))
          g.addEdge(v3.id, new StateForwarderEdge(v2.id))
          g.execute
          val idStateMap = g.aggregate[Map[Int, Int]](new IdStateMapAggregator[Int, Int])
          for (i <- Set(0, 1, 2, 3)) {
            for (j <- Set(0, 1, 2, 3)) {
              if (i != j) {
                assert(idStateMap(i) != idStateMap(j), s"Vertex $i and vertex $j have a color collision.")
              }
            }
          }
          g.shutdown
          true
        } catch {
          case t: Throwable =>
            t.printStackTrace
            true
        }
      },
      minSuccessful(10))
  }

  "RankedDsaA" should "correctly assign colors to a random grid" in {
    check(

      (wWidth: Width) => {
        val width = wWidth.w
        runId += 1
        try {
          assert(width <= 40, s"Width $width is bigger than 40.")
          println(s"STARTING TEST RUN $runId")
          val g = GraphBuilder.build
          val optimizer = DsaAVertexColoring(changeProbability = 1.0)
          val domain = (0 to 3).toSet
          def randomFromDomain = domain.toSeq(Random.nextInt(domain.size))
          val debug = false

          for (i <- 0 until width * width)
            g.addVertex(new SimpleDcopVertex(i, domain, optimizer, randomFromDomain, debug = debug))

          for (i <- 0 until width * width)
            for (n <- neighbours(i, width))
              g.addEdge(i, new StateForwarderEdge(n))

          g.execute
          val idStateMap = g.aggregate[Map[Int, Int]](new IdStateMapAggregator[Int, Int])
          for (i <- 0 until width) {
            for (j <- neighbours(i, width)) {
              val iConfig = g.forVertexWithId(i, (x: SimpleDcopVertex[Int, Int]) => x.currentConfig)
              val jConfig = g.forVertexWithId(j, (x: SimpleDcopVertex[Int, Int]) => x.currentConfig)
              val iLocalOptimum = g.forVertexWithId(i, (x: SimpleDcopVertex[Int, Int]) => x.optimizer.isConverged(x.currentConfig))
              val jLocalOptimum = g.forVertexWithId(j, (x: SimpleDcopVertex[Int, Int]) => x.optimizer.isConverged(x.currentConfig))
              assert(idStateMap(i) != idStateMap(j) || ((iLocalOptimum) && (jLocalOptimum)),
                s" \nGrid size: $width.\n " +
                  s" Vertex $i" +
                  s" with configuration: \n$iConfig" +
                  s" with isLocalOptimum $iLocalOptimum" +
                  s" and \n Vertex $j" +
                  s" with configuration: \n$jConfig" +
                  s" with isLocalOptimum $jLocalOptimum have a color collision and are not in Local Optima.")
            }
          }
          g.shutdown
          true
        } catch {
          case t: Throwable =>
            t.printStackTrace
            true
        }
      },
      minSuccessful(10))
  }
}



