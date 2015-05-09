package representation

import utils.ImplicitConversions.toEnhancedTraversable

case class MultiVoicePhrase(phrases: Traversable[Phrase]) extends MusicalElement {
  override def getDuration: Double = phrases.numericFold[Double](0.0, _.getDuration)
}
