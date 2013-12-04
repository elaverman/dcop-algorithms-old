package com.signalcollect.dcopthesis

import language.higherKinds
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import org.scalacheck.Gen._
import org.scalatest.FlatSpec
import org.scalatest.ShouldMatchers
import org.scalatest.prop.Checkers
import org.scalatest.mock.EasyMockSugar
import com.signalcollect.examples.PageRankVertex
import com.signalcollect.examples.PageRankEdge
import com.signalcollect.interfaces.SignalMessage
import com.signalcollect.GraphEditor
import com.signalcollect.GraphBuilder
import scala.util.Random

class RankedDSASpec extends FlatSpec with ShouldMatchers with Checkers with EasyMockSugar {

  lazy val smallInt = Gen.chooseNum(0, 100)
  lazy val smallDouble = Gen.chooseNum(0.0, 10.0)

  lazy val signalMapEntry = for {
    k <- smallInt
    v <- smallDouble
  } yield (k, v)

  lazy val signalMap = containerOf[Map, Int, Double](signalMapEntry)

  lazy val outEdgeIds = containerOf[Set, Int](smallInt)

  implicit def arbSignalMap[Map[Int, Double]] = Arbitrary(signalMap)

  implicit def arbEdgeIds[Set[Int]] = Arbitrary(outEdgeIds)

 
//TODO modify. Test was ported from the old project...

//  "RankedDSAVertex" should "correctly collect and signal the rank" in {
//    check(
//      (incomingSignals: Map[Int, Double]) => {
//        try {
//          println("************1 vertex test for rank**********")
//          val id = 0
//          val incomingWithoutSelf = incomingSignals.filter(_._1 != id)
//          val outgoingEdges = incomingWithoutSelf.keys
//          val domain = Array(0, 1)
//          val mockGraphEditor = mock[GraphEditor[Any, Any]]
//          val v = new RdsaVertex(id, 0, domain) with ArgmaxADecision[Int] with PageRankParallelRandomSchedule {
//            def p = rank
//          }
//          for (targetId <- outgoingEdges) {
//            v.addEdge(new RdsaEdge(targetId), mockGraphEditor)
//          }
//          v.afterInitialization(mockGraphEditor)
//          for ((sourceId, rankSignal) <- incomingWithoutSelf) {
//            v.deliverSignal((1, rankSignal), Some(sourceId), mockGraphEditor)
//          }
//          if (!incomingWithoutSelf.isEmpty) {
//            assert(v.scoreCollect > 0, "vertex received messages, should want to collect")
//            v.executeCollectOperation(mockGraphEditor)
//            v.rank should equal(0.35 + 0.65 * incomingWithoutSelf.values.sum +- 0.0001)
//            if (!outgoingEdges.isEmpty) {
//              assert(v.scoreSignal > 0, "vertex updated state, should want to signal")
//              expecting {
//                for (targetId <- outgoingEdges) {
//                  call(mockGraphEditor.sendToWorkerForVertexIdHash(
//                    SignalMessage(targetId, Some(id), (v.state, v.rank / outgoingEdges.size)), targetId.hashCode))
//                }
//              }
//              whenExecuting(mockGraphEditor) {
//                v.executeSignalOperation(mockGraphEditor)
//              }
//            }
//          } else {
//            println("incomingWithoutSelf is empty")
//          }
//          true
//        } catch {
//          case t: Throwable =>
//            t.printStackTrace
//            throw t
//        }
//      }, minSuccessful(10))
//  }

}
