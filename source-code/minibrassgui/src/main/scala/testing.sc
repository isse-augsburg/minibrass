case class C(name: Int)

case class CG(val cr: Map[C, List[C]]) {
  def addCR(newCR: Map[C, List[C]]) : CG =
    CG(cr ++ newCR)
}


val test = CG(Map[C, List[C]](
  C(1)->List[C](C(2), C(3)),
  C(2)->List[C](C(0)),
  C(3)->List[C](C(0))
))

test

val test2 = test.addCR(Map[C, List[C]](
  C(4)->List[C](C(0))
))

test2.cr

test.cr
