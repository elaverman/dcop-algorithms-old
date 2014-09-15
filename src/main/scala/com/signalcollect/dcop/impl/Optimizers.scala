package com.signalcollect.dcop.impl

import com.signalcollect.dcop.modules._

class BasicOptimizer[AgentId, Action, Config <: Configuration[AgentId, Action], UtilityType] extends Optimizer[AgentId, Action, Configuration[AgentId, Action], Double]

class SimpleOptimizer[AgentId, Action] extends Optimizer[AgentId, Action, SimpleConfig[AgentId, Action], Double]

class RankedOptimizer[AgentId, Action] extends Optimizer[AgentId, Action, RankedConfig[AgentId, Action], Double]