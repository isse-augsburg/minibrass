package minibrassgui

import minibrassgui.ConstraintGraph.ConstraintRelation


sealed case class Constraint(name: String)

/**
  * Class holding the Constraint Preferences structure.
  * @param constraintRelations key: less important constraint; value: more important constraint(s) or None
  */
sealed case class ConstraintGraph(val constraintRelations: ConstraintRelation){
  /**
    * Returns the new ConstraintGraph after adding some Constraints and ConstraintRelations in case the resulting
    * Graph is still acyclic.
    * @param newCR data to add
    * @return new ConstraintGraph in case adding was successful, the old one otherwise
   */
  def addCR(newCR: ConstraintRelation) : ConstraintGraph =
    ConstraintGraph(constraintRelations ++ newCR)

  /**
    * Checks whether the given Graph is acyclic.
    * @return true if DAG is acyclic;
    */
  def ensureGraphIsAcyclic(g: ConstraintGraph) : Boolean = ???
}

object ConstraintGraph {
  type ConstraintRelation = Map[Constraint, List[Constraint]]
}