package cbr

trait Feature {
  /**
   * Returns the size of this feature in terms of the size of its signature
   * @return
   */
  def size: Int

  /**
   * Converts this feature to an array of doubles (its signature)
   * @return
   */
  def getSignature: Array[Double]
}
