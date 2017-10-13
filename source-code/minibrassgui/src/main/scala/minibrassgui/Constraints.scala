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
  def ensureGraphIsAcyclic(constrRels: ConstraintRelation) : Boolean = {
    /**
      * Checks whether the given key is not cyclic.
      * @param constraint the given key
      * @return true if acyclic
      */
    def checkKey(constraint: Constraint, keyToCheck: Constraint) : Boolean = {
      val keysToCheck : Option[List[Constraint]] = constrRels.get(constraint)
      if (keysToCheck.get.isEmpty) {true}
      else if (keysToCheck.get.contains(keyToCheck)) {false}
      else {
        (for{key <- keysToCheck.get} yield checkKey(key, keyToCheck)).forall (x => x)
      }
    }

    (for(key <- constrRels.keys) yield checkKey(key, key)).forall(x => x)
  }
}

object ConstraintGraph {
  type ConstraintRelation = Map[Constraint, List[Constraint]]
}