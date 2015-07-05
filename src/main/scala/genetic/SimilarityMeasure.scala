package genetic

import cbr.description.PhraseDescriptionCreators
import utils.NumericUtils


object SimilarityMeasures {
  import cbr.MusicalCase
  def musicalCaseSimilarity: SimilarityMeasure[MusicalCase] = {
    new SimilarityMeasure[MusicalCase] {
      override def similarity(o1: MusicalCase, o2: MusicalCase): Double = {
        val d1 = PhraseDescriptionCreators.getDefault.createCaseDescription(o1)
        val d2 = PhraseDescriptionCreators.getDefault.createCaseDescription(o2)
        val s1 = d1.getSignature
        val s2 = d2.getSignature

        NumericUtils.euclid(s1, s2)
      }
    }
  }
}

trait SimilarityMeasure[T] extends ((T, T) => Double) {
  override def apply(o1: T, o2: T): Double = similarity(o1, o2)
  def similarity(o1: T, o2: T): Double
}
