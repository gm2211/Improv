package cbr

import cbr.description.CaseDescription

trait CaseIndex[CD <: CaseDescription[Case], Case] extends Traversable[CaseDescription[Case]] {

  /**
   * Finds the `k` nearest neighbours of the specified case
   * @param caseDescription Description of the case
   * @param k number of neighbours to be retrieved
   * @return A traversable of up to `k` neighbours
   */
  def findKNearestNeighbours(caseDescription: CD, k: Int): Traversable[Case]

  /**
   * Adds a case to the index
   * @param caseDescription Description of the case
   * @param caseSolution Solution of the case
   */
  def addCase(caseDescription: CD, caseSolution: Case): Unit

  /**
   * Removes a case from the index
    * @param caseDescription Description of the case to be removed
   * @return true if successful, false otherwise (maybe trying to remove something that is not in the index)
   */
  def removeCase(caseDescription: CD): Boolean

  /**
   * Remove all entries
   * @return itself so that you can have a sort of fluent api
   */
  def clear(): CaseIndex.this.type

  /**
   * Compacts the index potentially removing entries marked for removal
   */
  def compact(): Unit
}
