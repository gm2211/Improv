package cbr

import net.sf.javaml.core.kdtree.KDTree


class KDTreeIndex[CD <: CaseDescription, CaseSolution](
    val store: CaseSolutionStore[CaseSolution],
    descriptionSize: Int) extends CaseIndex[CD, CaseSolution] {
  private val kdTree = new KDTree[String](descriptionSize)

  def addCase(caseDescription: CD, caseSolution: CaseSolution): Unit = {
    val storedSolutionID = store.addSolution(caseSolution)
    kdTree.insert(caseDescription.getSignature, storedSolutionID)
  }

  override def findKNearestNeighbours(caseDescription: CD, k: Int): Traversable[CaseSolution] = {
    kdTree.nearest(caseDescription.getSignature, k).flatMap { solutionID =>
      store.getSolution(solutionID)
    }
  }
}
