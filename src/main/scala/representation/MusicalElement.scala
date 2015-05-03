package representation

trait MusicalElement {
  /**
   * Returns true if a musical element has no content (e.g.: an empty Phrase)
   * @return
   */
  def isEmpty: Boolean = false
}
