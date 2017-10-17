package minibrassgui

import minibrassgui.ConstraintGraph.ConstraintRelation


sealed case class Constraint(name: String, weight: Int = 0)

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
    * @return true if DAG is acyclic; uses DFS
    */
  def ensureGraphIsAcyclic() : Boolean = {
    /**
      * Checks whether the given key is not cyclic.
      * @param constraint the given key
      * @return true if acyclic
      */
    def checkKey(constraint: Constraint, keysToCheck: List[Constraint]) : Boolean = {
      val values : Option[List[Constraint]] = this.constraintRelations.get(constraint).orElse(None)
      if (values == None) {true}
      else if (! (values.get.diff(keysToCheck) equals values.get)) false
      else {
        (for{key <- values.get} yield checkKey(key, keysToCheck ++ values.get)).forall (x => x)
      }
    }

    (for(key <- this.constraintRelations.keys) yield checkKey(key, List(key))).forall(x => x)
  }
}

object ConstraintGraph {
  type ConstraintRelation = Map[Constraint, List[Constraint]]
}