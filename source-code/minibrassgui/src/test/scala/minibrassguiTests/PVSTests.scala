package minibrassguiTests

import minibrassgui._

class PVSTests extends MinibrassguiSpec {

  test("PVS type hierarchy") {
    println(
      new PreferenceStructure("activities",
        Some(new PVSPareto("hobbies/work",
          Some(new PVSLex("dancing/singing",
            Some(new WeightedCSP("dancing", 45)),
            Some(new ConstraintPreferences("singing", true)))),
          Some(new CostFunctionNetwork("programming", 8, Maximize(), SquaredSum())))
        )))
  }

}
