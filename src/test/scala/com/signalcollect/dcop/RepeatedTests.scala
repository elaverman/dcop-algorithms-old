//package com.signalcollect.dcop
//
//import org.scalatest._ 
//
//trait RepeatedTests extends AbstractSuite { this: Suite => 
//
//  val DefaultCount = 10 
//
//  def repeatedTests: List[Tuple2[String, Int]] = (for (testName <- 
//testNames) yield (testName, DefaultCount)).toList 
//
//  abstract override def runTest(testName: String, reporter: Reporter, 
//stopper: Stopper, configMap: Map[String, Any], tracker: Tracker) { 
//
//    val count = 
//      repeatedTests.filter(_._1 == testName) match { 
//        case Nil => 1 
//        case list => // should be one but could be more by mistake, if so just take the first 
//          list.head._2 
//      } 
//
//    for (i <- 0 until count) 
//      super.runTest(testName, new Args(reporter, stopper, configMap, tracker))
//  } 
//} 