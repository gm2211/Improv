package utils.functional

import scala.collection.mutable

class MemoizedFunc[From, To](private val function: From => To) {
  private val results: mutable.Map[From, To] = new mutable.HashMap()

  /**
   * Returns the memoized value of the function if available for the input,
   * otherwise it computes it and stores it before returning it
   * @param from Input on which the function should be computed
   * @param refresh Recomputes the value of the function regardless of whether it is stored or not
   * @return The function result
   */
  def apply(from: From, refresh: Boolean = false): To = {
    if (refresh || ! results.contains(from)) {
      results.put(from, function(from))
    }
    results.get(from).get
  }

  /**
   * Updates the cached value To for From
   * @param from input
   * @param to output to be cached
   */
  def update(from: From, to: To) = results.put(from, to)

}
