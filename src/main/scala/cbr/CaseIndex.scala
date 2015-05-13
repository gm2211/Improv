package cbr

trait CaseIndex[CD <: CaseDescription, CaseSolution] {
  def addCase(caseDescription: CD, caseSolution: CaseSolution): Unit
  def findKNearestNeighbours(caseDescription: CD, k: Int): Traversable[CaseSolution]
}
