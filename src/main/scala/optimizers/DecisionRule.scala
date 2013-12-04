package optimizers

import scala.util.Random

trait ConfigEvaluator[VariableType, ValueType, C <: Configuration[VariableType, ValueType]] {
  type Config = C
  def utility(c: Config): Double
}

trait Configuration[VariableIdType, ValueType] {
  def neighborhood: Map[VariableIdType, ValueType]
  def domain: Set[ValueType]
  def withCentralVariableAssignment(value: ValueType): Configuration[VariableIdType, ValueType]
  def centralVariableAssignment: (VariableIdType, ValueType)
  def centralVariableValue = centralVariableAssignment._2
}

trait VertexColoringConfigEvaluator extends ConfigEvaluator[Int, Int, Configuration[Int, Int]] {
  def utility(c: Config): Double = {
    val occupiedColors = c.neighborhood.values
    val numberOfConflicts = occupiedColors.filter(_ == c.centralVariableValue).size
    val numberOfNeighbors = occupiedColors.size
    val neighborsInSync = numberOfNeighbors - numberOfConflicts
    neighborsInSync
  }
}

trait DecisionRule[VariableType, ValueType] extends ConfigEvaluator[VariableType, ValueType, Configuration[VariableType, ValueType]] {
  def chooseNewAssignment(currentConfiguration: Config): ValueType
}

trait ElasSchedulerTrait[SomethingINeedToDecideHowToSchedule] { //extends ConfigEvaluator[Int, Int, Configuration[Int, Int]]
  def shouldIDoSomething(c: SomethingINeedToDecideHowToSchedule): Boolean
}

trait ArgmaxA extends DecisionRule[Int, Int] with ElasSchedulerTrait[Any] {
  def changeThreshold: Double
  def chooseNewAssignment(currentConfiguration: Config): Int = {
    val configurationCandidates: Set[Config] = for {
      assignment <- currentConfiguration.domain
    } yield currentConfiguration.withCentralVariableAssignment(assignment)
    val configUtilities: Map[Config, Double] = configurationCandidates.map(c => (c, utility(c))).toMap
    val maxUtility = configUtilities.values.max
    val maxUtilityConfigs: Seq[Config] = configUtilities.filter(_._2 == maxUtility).map(_._1).toSeq
    val currentUtility = configUtilities(currentConfiguration)
    //val shouldChangeAssignment = Random.nextDouble < changeThreshold
    val shouldChangeAssignment = shouldIDoSomething("SomethingUseful") //TODO: move it away
    if (currentUtility < maxUtility && shouldChangeAssignment) {
      val chosenMaxUtilityConfig = maxUtilityConfigs(Random.nextInt(maxUtilityConfigs.size))
      chosenMaxUtilityConfig.centralVariableValue
    } else {
      currentConfiguration.centralVariableValue
    }
  }
}

trait ElasElaborateSchedulerImplementation extends ElasSchedulerTrait[Any] {
  def shouldIDoSomething(c: Any): Boolean = false // GRUMPYCAT: NOOOOOO!
}

case class VertexColoringDsaA(val changeThreshold: Double) extends ArgmaxA with VertexColoringConfigEvaluator with ElasElaborateSchedulerImplementation

//case class RankedDsaA extends ArgmaxA with VertexColoringConfigEvaluator {
//  def changeThreshold = {
//    0
//
//    ???
//  }
//}

case class VertexColoringConfig(
  val neighborhood: Map[Int, Int],
  val domain: Set[Int],
  val centralVariableAssignment: (Int, Int)) extends Configuration[Int, Int] {
  def withCentralVariableAssignment(value: Int): Configuration[Int, Int] = {
    this.copy(centralVariableAssignment = (centralVariableAssignment._1, value))
  }
}
