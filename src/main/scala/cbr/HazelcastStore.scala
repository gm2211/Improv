package cbr

class HazelcastStore[CaseSolution] extends CaseSolutionStore[CaseSolution] {
  override def addSolution(caseSolution: CaseSolution): String = ""

  override def getSolution(solutionReference: String): Option[CaseSolution] = None

  override def removeSolution(solutionReference: String): Unit = ()
}
