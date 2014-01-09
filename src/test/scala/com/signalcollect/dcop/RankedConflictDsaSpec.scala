//package com.signalcollect.dcop
//
//import scala.language.higherKinds
//import scala.util.Random
//import org.scalacheck.Arbitrary
//import org.scalacheck.Arbitrary.arbInt
//import org.scalacheck.Gen
//import org.scalacheck.Gen.containerOf
//import org.scalatest.FlatSpec
//import org.scalatest.ShouldMatchers
//import org.scalatest.mock.EasyMockSugar
//import org.scalatest.prop.Checkers
//import com.signalcollect.GraphBuilder
//import com.signalcollect.IdStateMapAggregator
//import com.signalcollect.StateForwarderEdge
//import com.signalcollect.dcop.graph.RankedDcopVertex
//import com.signalcollect.dcop.modules._
//import com.signalcollect.dcop.impl._
//import com.signalcollect.dcop.graph.RankedVertexColoringEdge
//import com.signalcollect.dcop.evaluation.Grid
//
//case class Width(val w: Int) extends AnyVal
//
//class RankedConflictDsaSpec extends FlatSpec with ShouldMatchers with Checkers with EasyMockSugar {
//
//  lazy val smallInt = Gen.chooseNum(0, 100)
//  lazy val smallDouble = Gen.chooseNum(0.0, 10.0)
//
//  lazy val signalMapEntry = for {
//    k <- smallInt
//    v <- smallDouble
//  } yield (k, v)
//
//  lazy val signalMap = containerOf[Map, Int, Double](signalMapEntry)
//
//  lazy val outEdgeIds = containerOf[Set, Int](smallInt)
//
//  implicit def arbSignalMap[Map[Int, Double]] = Arbitrary(signalMap)
//
//  implicit def arbEdgeIds[Set[Int]] = Arbitrary(outEdgeIds)
//
//  lazy val smallWidth = Gen.chooseNum(1, 10).map(Width(_))
//  implicit def arbSmallWidth[Width] = Arbitrary(smallWidth)
//
//  var runId = 0
//
//  "RankedConflictDsaB" should "correctly assign colors to a small test graph" in {
//    check(
//      (irrelevantParameter: Int) => {
//        runId += 1
//        try {
//          println(s"STARTING TEST RUN $runId")
//          val g = GraphBuilder.build
//          val optimizer: OptimizerModule[Int, Int] with RankedConfiguration[Int, Int] = RankedDsaBVertexColoring(changeProbability = 0.7)
//          val domain = (0 to 1).toSet
//          def randomFromDomain = domain.toSeq(Random.nextInt(domain.size))
//          val debug = false
//          val v0 = new RankedDcopVertex(0, domain, optimizer, randomFromDomain, debug = debug)
//          val v1 = new RankedDcopVertex(1, domain, optimizer, randomFromDomain, debug = debug)
//          val v2 = new RankedDcopVertex(2, domain, optimizer, randomFromDomain, debug = debug)
//          g.addVertex(v0)
//          g.addVertex(v1)
//          g.addVertex(v2)
//          g.addEdge(v0.id, new RankedVertexColoringEdge(v1.id))
//          g.addEdge(v1.id, new RankedVertexColoringEdge(v0.id))
//          g.addEdge(v0.id, new RankedVertexColoringEdge(v2.id))
//          g.addEdge(v2.id, new RankedVertexColoringEdge(v0.id))
//          g.execute
//          val idStateMap = g.aggregate[Map[Int, (Int, Double)]](new IdStateMapAggregator[Int, (Int, Double)])
//          assert(idStateMap(0)._1 != idStateMap(1)._1, "Vertex 0 and vertex 1 have a color collision.")
//          assert(idStateMap(0)._1 != idStateMap(2)._1, "Vertex 0 and vertex 2 have a color collision.")
//          g.shutdown
//          true
//        } catch {
//          case t: Throwable =>
//            t.printStackTrace
//            true
//        }
//      },
//      minSuccessful(1))
//  }
//
//  "RankedConflictDsaB" should "correctly assign colors to a 2x2 grid" in {
//    check(
//      (irrelevantParameter: Int) => {
//        runId += 1
//        try {
//          println(s"STARTING TEST RUN $runId")
//          val g = GraphBuilder.build
//          val optimizer: OptimizerModule[Int, Int] with RankedConfiguration[Int, Int] = RankedDsaBVertexColoring(changeProbability = 0.7)
//          val domain = (0 until 4).toSet
//          def randomFromDomain = domain.toSeq(Random.nextInt(domain.size))
//          val debug = false
//          val v0 = new RankedDcopVertex(0, domain, optimizer, randomFromDomain, debug = debug)
//          val v1 = new RankedDcopVertex(1, domain, optimizer, randomFromDomain, debug = debug)
//          val v2 = new RankedDcopVertex(2, domain, optimizer, randomFromDomain, debug = debug)
//          val v3 = new RankedDcopVertex(3, domain, optimizer, randomFromDomain, debug = debug)
//          g.addVertex(v0)
//          g.addVertex(v1)
//          g.addVertex(v2)
//          g.addVertex(v3)
//          g.addEdge(v0.id, new RankedVertexColoringEdge(v1.id))
//          g.addEdge(v0.id, new RankedVertexColoringEdge(v2.id))
//          g.addEdge(v0.id, new RankedVertexColoringEdge(v3.id))
//          g.addEdge(v1.id, new RankedVertexColoringEdge(v0.id))
//          g.addEdge(v1.id, new RankedVertexColoringEdge(v2.id))
//          g.addEdge(v1.id, new RankedVertexColoringEdge(v3.id))
//          g.addEdge(v2.id, new RankedVertexColoringEdge(v0.id))
//          g.addEdge(v2.id, new RankedVertexColoringEdge(v1.id))
//          g.addEdge(v2.id, new RankedVertexColoringEdge(v3.id))
//          g.addEdge(v3.id, new RankedVertexColoringEdge(v0.id))
//          g.addEdge(v3.id, new RankedVertexColoringEdge(v1.id))
//          g.addEdge(v3.id, new RankedVertexColoringEdge(v2.id))
//          g.execute
//          val idStateMap = g.aggregate[Map[Int, (Int, Double)]](new IdStateMapAggregator[Int, (Int, Double)])
//          for (i <- Set(0, 1, 2, 3)) {
//            for (j <- Set(0, 1, 2, 3)) {
//              if (i != j) {
//                assert(idStateMap(i)._1 != idStateMap(j)._1, s"Vertex $i with rank ${g.forVertexWithId(i, (x: RankedDcopVertex[Int, Int]) => x.state._2)}" +
//                  s"and vertex $j with rank ${g.forVertexWithId(j, (x: RankedDcopVertex[Int, Int]) => x.state._2)} have a color collision.")
//              }
//            }
//          }
//          g.shutdown
//          true
//        } catch {
//          case t: Throwable =>
//            t.printStackTrace
//            true
//        }
//      },
//      minSuccessful(10))
//  }
//
//  "RankedConflictDsaB" should "correctly assign colors to a random grid" in {
//    check(
//
//      (wWidth: Width) => {
//        val width = wWidth.w
//        runId += 1
//        try {
//          assert(width <= 40, s"Width $width is bigger than 40.")
//          println(s"STARTING TEST RUN $runId")
//          val grid = Grid(width) 
//          val g = GraphBuilder.build
//          val optimizer: OptimizerModule[Int, Int] with RankedConfiguration[Int, Int] = RankedDsaBVertexColoring(changeProbability = 1.0)
//          val domain = (0 to 3).toSet
//          def randomFromDomain = domain.toSeq(Random.nextInt(domain.size))
//          val debug = false
//
//          for (i <- 0 until width * width)
//            g.addVertex(new RankedDcopVertex(i, domain, optimizer, randomFromDomain, debug = debug))
//
//          for (i <- 0 until width * width)
//            for (n <- grid.computeNeighbours(i))
//              g.addEdge(i, new RankedVertexColoringEdge(n))
//
//          g.execute
//          val idStateMap = g.aggregate[Map[Int, (Int, Double)]](new IdStateMapAggregator[Int, (Int, Double)])
//          for (i <- 0 until width) {
//            for (j <- grid.computeNeighbours(i)) {
//              //TODO: Add condition that it's in NE #of conflicts and NE target function
//              val iConfig = g.forVertexWithId(i, (x: RankedDcopVertex[Int, Int]) => x.currentConfig)
//              val jConfig = g.forVertexWithId(j, (x: RankedDcopVertex[Int, Int]) => x.currentConfig)
//              val iLocalOptimum = g.forVertexWithId(i, (x: RankedDcopVertex[Int, Int]) => x.optimizer.isConverged(x.currentConfig))
//              val jLocalOptimum = g.forVertexWithId(j, (x: RankedDcopVertex[Int, Int]) => x.optimizer.isConverged(x.currentConfig))
//              assert(idStateMap(i)._1 != idStateMap(j)._1 || ((iLocalOptimum) && (jLocalOptimum)),
//                s" \nGrid size: $width.\n " +
//                  s" Vertex $i" +
//                  s" with configuration: \n$iConfig" +
//                  s" with isLocalOptimum $iLocalOptimum" +
//                  s" with rank ${g.forVertexWithId(i, (x: RankedDcopVertex[Int, Int]) => x.state._2)}" +
//                  s" and \n Vertex $j" +
//                  s" with configuration: \n$jConfig" +
//                  s" with isLocalOptimum $jLocalOptimum" +
//                  s" with rank ${g.forVertexWithId(j, (x: RankedDcopVertex[Int, Int]) => x.state._2)} have a color collision.")
//            }
//          }
//          g.shutdown
//          true
//        } catch {
//          case t: Throwable =>
//            t.printStackTrace
//            true
//        }
//      },
//      minSuccessful(10))
//  }
//
//  //TODO modify. Test was ported from the old project...
//
//  //  "RankedDSAVertex" should "correctly collect and signal the rank" in {
//  //    check(
//  //      (incomingSignals: Map[Int, Double]) => {
//  //        try {
//  //          println("************1 vertex test for rank**********")
//  //          val id = 0
//  //          val incomingWithoutSelf = incomingSignals.filter(_._1 != id)
//  //          val outgoingEdges = incomingWithoutSelf.keys
//  //          val domain = Array(0, 1)
//  //          val mockGraphEditor = mock[GraphEditor[Any, Any]]
//  //          val v = new RdsaVertex(id, 0, domain) with ArgmaxADecision[Int] with PageRankParallelRandomSchedule {
//  //            def p = rank
//  //          }
//  //          for (targetId <- outgoingEdges) {
//  //            v.addEdge(new RdsaEdge(targetId), mockGraphEditor)
//  //          }
//  //          v.afterInitialization(mockGraphEditor)
//  //          for ((sourceId, rankSignal) <- incomingWithoutSelf) {
//  //            v.deliverSignal((1, rankSignal), Some(sourceId), mockGraphEditor)
//  //          }
//  //          if (!incomingWithoutSelf.isEmpty) {
//  //            assert(v.scoreCollect > 0, "vertex received messages, should want to collect")
//  //            v.executeCollectOperation(mockGraphEditor)
//  //            v.rank should equal(0.35 + 0.65 * incomingWithoutSelf.values.sum +- 0.0001)
//  //            if (!outgoingEdges.isEmpty) {
//  //              assert(v.scoreSignal > 0, "vertex updated state, should want to signal")
//  //              expecting {
//  //                for (targetId <- outgoingEdges) {
//  //                  call(mockGraphEditor.sendToWorkerForVertexIdHash(
//  //                    SignalMessage(targetId, Some(id), (v.state, v.rank / outgoingEdges.size)), targetId.hashCode))
//  //                }
//  //              }
//  //              whenExecuting(mockGraphEditor) {
//  //                v.executeSignalOperation(mockGraphEditor)
//  //              }
//  //            }
//  //          } else {
//  //            println("incomingWithoutSelf is empty")
//  //          }
//  //          true
//  //        } catch {
//  //          case t: Throwable =>
//  //            t.printStackTrace
//  //            throw t
//  //        }
//  //      }, minSuccessful(10))
//  //  }
//
//}
