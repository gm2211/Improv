package cbr.description.features

class SimpleFeature[Element](private val array: Array[Double]) extends Feature[Element] {
  /**
   * Converts this feature to an array of doubles (its signature)
   * @return
   */
  override def getSignature: Array[Double] = array

  /**
   * Returns the size of this feature in terms of the size of its signature
   * @return
   */
  override def size: Int = array.length
}
