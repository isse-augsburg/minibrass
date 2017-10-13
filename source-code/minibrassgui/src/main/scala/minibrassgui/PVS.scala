package minibrassgui

import minibrassgui.PVSComp.CompParts

/**
  * The following case classes are all available PVS types.
 *
  * @param name Name of the PVS
  */
case class CostFunctionNetwork(name: String) extends PVSLeaf(name) {
  override def visualiseEntity(): Any = ???
}
case class MaxCSP(name: String) extends PVSLeaf(name) {
  override def visualiseEntity(): Any = ???
}
case class ConstraintPreferences(name: String, useSPD: Boolean) extends PVSLeaf(name){
  override def visualiseEntity(): Any = ???
}
case class WeightedCSP(name: String) extends PVSLeaf(name){
  override def visualiseEntity(): Any = ???
}
case class ProbabilisticCSP(name: String) extends PVSLeaf(name){
  override def visualiseEntity(): Any = ???
}
case class FuzzyCSP(name: String) extends PVSLeaf(name){
  override def visualiseEntity(): Any = ???
}


/**
  * A PVS is either a Leaf...
  */
abstract class PVSLeaf(name: String) extends PVS(name){
  override def visualiseEntity(): Any = ???
}

/**
  * ...or a Composition of PVS's
  * @param consistsOf A PVSComp consists of two PVS's. Right after creation of 'this', these do not yet exist.
  */
case class PVSComp(name: String, consistsOf: Option[CompParts] = None) extends PVS(name){
  override def visualiseEntity(): Any = ???

  def getChildren(): Option[CompParts] = consistsOf

  def setChildren(compParts: CompParts) = PVSComp(name, Some(compParts))
}

object PVSComp {
  type CompParts = (PVS, PVS)
}

/**
  * Basic PVS class.
  */
sealed abstract class PVS(name: String) extends Visualise

/**
  * Every PVS must be visualised. The trait's function should return an appropriate graphical element.
  */
abstract trait Visualise {
  def visualiseEntity() : Any
}
