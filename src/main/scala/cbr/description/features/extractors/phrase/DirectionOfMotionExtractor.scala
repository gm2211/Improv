package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import representation.Phrase

import scala.util.Try

class DirectionOfMotionExtractor extends PhraseFeatureExtractor {
  override def extractFeatureFromNonPolyphonic(phrase: Phrase): Feature[Phrase] = {
    val intervals = Phrase.computeMelodicIntervals(phrase)
    val (ups, downs) = intervals.partition(_ > 0)
    val direction = Try(ups.length.toDouble / (ups.length + downs.length)).getOrElse(0.0)
    Feature.from(direction)
  }

  override val featureSize: Int = 1

}
