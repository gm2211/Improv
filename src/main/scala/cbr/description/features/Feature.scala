package cbr.description.features

object Feature {
  def from[Element](value: Double)(implicit dummyImplicit: DummyImplicit): Feature[Element] = from[Element](Array(value))

  def from[Element](array: Array[Double]) = new SimpleFeature[Element](array)
}

trait Feature[Element] {
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
