package cbr

trait CaseSolutionStore[CaseSolution] {
  def addSolution(caseSolution: CaseSolution): String
  def removeSolution(solutionID: String): Unit
  def getSolution(solutionID: String): Option[CaseSolution]
}
