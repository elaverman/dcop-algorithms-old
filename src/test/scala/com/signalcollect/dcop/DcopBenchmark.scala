package com.signalcollect.dcop

import com.signalcollect.GraphBuilder
import com.signalcollect.dcop.graph.SimpleDcopVertex
import com.signalcollect.StateForwarderEdge
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.configuration.ExecutionMode

object DcopBenchmark extends App {
  readLine
  def initial0Value = 0
  val g = GraphBuilder.withHeartbeatInterval(20).build
  for (i <- (1 to 10000)) {
    val vertex0 = new SimpleDcopVertex(0, Set(0, 1), new SimpleOptimizer[Int, Int](0.5), initial0Value, debug = false)
    val vertex1 = new SimpleDcopVertex(1, Set(0, 1), new SimpleOptimizer[Int, Int](0.5), initial0Value, debug = false)
    g.addVertex(vertex0)
    g.addVertex(vertex1)
    g.addEdge(0, new StateForwarderEdge(1))
    g.addEdge(1, new StateForwarderEdge(0))
    println(g.execute)
    assert(vertex0.state != vertex1.state, "Color collision")
    g.reset
  }
  g.shutdown
}
