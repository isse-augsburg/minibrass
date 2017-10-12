package minibrassguiTests

import minibrassgui.{Constraint, ConstraintGraph}


class ConstraintsTests extends MinibrassguiSpec{

  val sharedConstraintGraph = ConstraintGraph(Map(
    Constraint("one")->List(Constraint("two"), Constraint("three")),
    Constraint("two")->List(Constraint("zero")),
    Constraint("three")->List(Constraint("zero"))
  ))

  test("Constraints.ConstraintGraph.addCR"){
    assertResult(ConstraintGraph(Map(
      Constraint("one")->List(Constraint("two"), Constraint("three")),
      Constraint("two")->List(Constraint("zero")),
      Constraint("three")->List(Constraint("zero")),
      Constraint("four")->List(Constraint("two"))
    ))) {
      sharedConstraintGraph.addCR(Map(Constraint("four")->List(Constraint("two"))))
    }
  }

  ignore("Constraints.ConstraintGraph.ensureGraphIsAcyclic"){

  }
}







