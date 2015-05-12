package cbr

trait CaseIndex {
  def findKNearestNeighbours(targetCase: Case, k: Int): Traversable[Case]
}
