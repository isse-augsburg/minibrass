package minibrassgui


/**
  * A PVS is either a leaf or...
  * The following case classes are all available PVS types.
 *
  * @param name Name of the PVS
  */
abstract class PVSLeaf(name: String) extends PVS(name) with Visualise {
  override def toString: String = "name: " + name +
    " type: " + this.getClass.toString.reverse.takeWhile(x => x != '.').reverse
}

class CostFunctionNetwork(val name: String, val k: Int, val optType: OptimisationType, val optFunc: OptimisationFunction)
  extends PVSLeaf(name){
  override def toString: String = super.toString + "\t(k: " + k + "\toptType: " + optType + " optFunc: " + optFunc + ")\n"
  override def visualiseEntity(): Any = ???
}

/**
  * Some case classes for controlling the parameters of PVSLeafs.
  * Especially: CFN
  */
abstract sealed class OptimisationType
case class Maximize() extends OptimisationType
case class Minimize() extends OptimisationType

abstract sealed class OptimisationFunction
case class Sum() extends OptimisationFunction
case class SquaredSum() extends OptimisationFunction
case class Maximum() extends OptimisationFunction

/**
  * case classes for FuzzyCSP
  */
abstract sealed class OptimisationFunctionFuzzy
case class Minimum() extends OptimisationFunctionFuzzy
case class Average() extends OptimisationFunctionFuzzy


class MaxCSP(val name: String) extends PVSLeaf(name){
  override def visualiseEntity(): Any = ???
}

/**
  * PVS type Constraint Preferences
  * @param name Name of the PVS
  * @param useSPD true if SPD, TPD if false
  */
class ConstraintPreferences(val name: String, val useSPD: Boolean) extends PVSLeaf(name){

  override def toString: String = super.toString + "\t(uses SPD: " + useSPD + ")\n"

  override def visualiseEntity(): Any = ???
}
class WeightedCSP(val name: String, val weight: Int) extends PVSLeaf(name){
  override def toString: String = super.toString + "\t(weight: " + weight + ")\n"
  override def visualiseEntity(): Any = ???
}
class ProbabilisticCSP(val name: String) extends PVSLeaf(name) {
  override def visualiseEntity(): Any = ???
}
class FuzzyCSP(val name: String, val optFunc: OptimisationFunctionFuzzy) extends PVSLeaf(name) {
  override def toString: String = super.toString + "\t(optFunc: " + optFunc + ")\n"
  override def visualiseEntity(): Any = ???
}

/**
  * ...or a Composition of PVS's
  */
abstract class PVSComp(name: String) extends PVS(name) with Visualise {
  override def toString: String = "name: " + name +
    " type: " + this.getClass.toString.reverse.takeWhile(x => x != '.').reverse + "\n"
}

/**
  * specific compositions.
  * @param name name of the pvs instance
  */
class PVSLex(val name: String, val left: Option[PVS] = None, val right: Option[PVS] = None) extends PVSComp(name) {

  override def toString: String = {
    super.toString +
    "left:\t" + left.getOrElse("not specified").toString +
    "right:\t" + right.getOrElse("not specified").toString
  }

  override def visualiseEntity(): Any = ???
}

class PVSPareto(val name: String, val upper: Option[PVS] = None, val bottom: Option[PVS] = None) extends PVSComp(name) {

  override def toString: String = {
    super.toString +
    "upper:\t" + upper.getOrElse("not specified").toString +
    "bottom:\t" + bottom.getOrElse("not specified").toString
  }

  override def visualiseEntity(): Any = ???
}

/**
  * Root element of a new Preference structure
  * @param name name of the preference structure
  */
sealed class PreferenceStructure(val name: String, val consistsOf: Option[PVS] = None) extends PVS(name){
  override def toString: String = "name: " + name + "\nconsistsOf:\n" + consistsOf.getOrElse("not specified").toString
}

/**
  * Basic PVS class.
  */
sealed abstract class PVS(name: String){
  override def toString: String
}


/**
  * Every PVS must be visualised. The trait's function should return an appropriate graphical element.
  */
abstract trait Visualise {
  def visualiseEntity() : Any
}
