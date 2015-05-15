package cbr

trait CaseIndex[CD <: CaseDescription, CaseSolution] extends Traversable[CaseDescription] {
  def addCase(caseDescription: CD, caseSolution: CaseSolution): Unit
  def removeCase(caseDescription: CD): Boolean
  def clear(): Unit
  def findKNearestNeighbours(caseDescription: CD, k: Int): Traversable[CaseSolution]
}
