package minibrassguiTests

import minibrassgui.{Constraint, ConstraintGraph}


class ConstraintsTests extends MinibrassguiSpec{

  val sharedConstraintGraph = ConstraintGraph(Map(
    Constraint("one")->List(Constraint("two"), Constraint("three")),
    Constraint("two")->List.empty,
    Constraint("three")->List.empty
  ))
  val sharedConstraintGraph2 = ConstraintGraph(Map(
    Constraint("one")->List(Constraint("two"), Constraint("three")),
    Constraint("two")->List.empty,
    Constraint("three")->List.empty,
    Constraint("four")->List(Constraint("two"))
  ))

  test("Constraints.ConstraintGraph.addCR"){
    assertResult(sharedConstraintGraph2) {
      sharedConstraintGraph.addCR(Map(Constraint("four")->List(Constraint("two"))))
    }
  }
  test("Constraint.ConstraintGraph.addCR with ensureGraphIsAcyclic == false"){
    assertResult(sharedConstraintGraph){
      sharedConstraintGraph.addCR(Map(Constraint("three")->List(Constraint("one"))))
    }
  }

  test("Constraints.ConstraintGraph.ensureGraphIsAcyclic"){
    assertResult(true){
      sharedConstraintGraph.ensureGraphIsAcyclic(sharedConstraintGraph.constraintRelations)
    }
  }

  test("Constraints.ConstraintGraph.ensureGraphIsAcyclic test FALSE"){
    val badGraph = ConstraintGraph(Map(
      Constraint("one")->List(Constraint("two"), Constraint("three")),
      Constraint("two")->List(Constraint("three")),
      Constraint("three")->List(Constraint("four")),
      Constraint("four")->List(Constraint("two")),
    ))
    assertResult(false) {
      badGraph.ensureGraphIsAcyclic(badGraph.constraintRelations)
    }
  }

  test("Constraints.ConstraintGraph.ensureGraphIsAcyclic test empty"){
    val emptyGraph = ConstraintGraph(Map.empty)
    assertResult(true) {
      emptyGraph.ensureGraphIsAcyclic(emptyGraph.constraintRelations)
    }
  }
}







