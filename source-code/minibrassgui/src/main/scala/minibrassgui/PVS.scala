package minibrassgui


class CostFunctionNetwork(name: String) extends PVSLeaf(name)
class MaxCSP(name: String) extends PVSLeaf(name)
class ConstraintPreferences(name: String, useSPD: Boolean) extends PVSLeaf(name)
class WeightedCSP(name: String) extends PVSLeaf(name)
class PropabilisticCSP(name: String) extends PVSLeaf(name)
class FuzzyCSP(name: String) extends PVSLeaf(name)


/**
  * A PVS is either a Leaf...
  */
abstract class PVSLeaf(name: String) extends PVS(name)

/**
  * ...or a Composition of PVS
  */
abstract class PVSComp(name: String) extends PVS(name)

/**
  * Basic PVS class.
  */
sealed abstract class PVS(name: String)
