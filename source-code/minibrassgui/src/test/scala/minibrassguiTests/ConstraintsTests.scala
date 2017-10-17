package minibrassguiTests

import minibrassgui.{Constraint, ConstraintGraph}


class ConstraintsTests extends MinibrassguiSpec{

  val sharedConstraintGraph = ConstraintGraph(Map(
    Constraint("one")->List(Constraint("two"), Constraint("three")),
    Constraint("two")->List.empty,
    Constraint("three")->List.empty
  ))

  test("Constraints.ConstraintGraph.addCR"){
    assertResult(ConstraintGraph(Map(
      Constraint("one")->List(Constraint("two"), Constraint("three")),
      Constraint("two")->List.empty,
      Constraint("three")->List.empty,
      Constraint("four")->List(Constraint("two"))
    ))) {
      sharedConstraintGraph.addCR(Map(Constraint("four")->List(Constraint("two"))))
    }
  }

  test("Constraints.ConstraintGraph.ensureGraphIsAcyclic"){
    assertResult(true){
      sharedConstraintGraph.ensureGraphIsAcyclic()
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
      badGraph.ensureGraphIsAcyclic()
    }
  }

  test("Constraints.ConstraintGraph.ensureGraphIsAcyclic test empty"){
    val emptyGraph = ConstraintGraph(Map.empty)
    assertResult(true) {
      emptyGraph.ensureGraphIsAcyclic()
    }
  }
}







