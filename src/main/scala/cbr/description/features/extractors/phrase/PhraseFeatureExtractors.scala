package cbr.description.features.extractors.phrase

import cbr.description.features.extractors.{CombinedWeightedFeatureExtractor, SingleFeatureExtractor}
import representation.Phrase

object PhraseFeatureExtractors {
  def getDefault = {
    new CombinedWeightedFeatureExtractor[Phrase](getDefaultSimpleExtractors)
  }

  def getDefaultSimpleExtractors: List[SingleFeatureExtractor[Phrase]] = {
    List(
      new AverageNoteDurationExtractor,
      new AverageMelodicIntervalExtractor,
      new AverageTimeBetweenAttacksExtractor,
      new BasicPitchHistogramExtractor,
      new DirectionOfMotionExtractor,
      new DurationExtractor,
      new AverageDurationExtractor,
      new MinimumDurationExtractor,
      new MaximumDurationExtractor,
      new MaximumPitchExtractor,
      new MinimumPitchExtractor,
      new IntervalBetweenMostCommonPitchesExtractor,
      new MostCommonMelodicIntervalExtractor,
      new NoteDensityExtractor,
      new RepeatedNotesExtractor,
      new VariabilityOfNoteDurationExtractor,
      new VariabilityOfTimeBetweenAttacksExtractor
    )
  }

}
