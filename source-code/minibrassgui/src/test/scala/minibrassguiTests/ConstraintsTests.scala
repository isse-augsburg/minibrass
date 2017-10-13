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

  ignore("Constraints.ConstraintGraph.ensureGraphIsAcyclic"){
    assertResult(true){
      sharedConstraintGraph.ensureGraphIsAcyclic(sharedConstraintGraph.constraintRelations)
    }
  }
  test("Constraints.ConstraintGraph.ensureGraphIsAcyclic test FALSE"){
    val badGraph = ConstraintGraph(Map(
      Constraint("one")->List(Constraint("two"), Constraint("three"), Constraint("four")),
      Constraint("two")->List.empty,
      Constraint("three")->List(Constraint("four")),
      Constraint("four")->List(Constraint("one")),  // TODO here is a problem: if we set three instead of one then
                                                    // the algo goes in circles because it only considers the root one
                                                    // change: bring along a list and always compare to the list.
    ))
    assertResult(true) {
      badGraph.ensureGraphIsAcyclic(badGraph.constraintRelations)
    }
  }
}







