package com.signalcollect.dcop.evaluation

import com.signalcollect.dcop.graph._
import com.signalcollect.dcop.modules.OptimizerModule
import com.signalcollect._
import java.util.Date
import com.signalcollect.dcop._

case class RunStats(var avgGlobalVsOpt: Option[Double], optUtility: Int, var timeToFirstLocOptimum: Option[Int]){
  avgGlobalVsOpt = None
  timeToFirstLocOptimum = None
}