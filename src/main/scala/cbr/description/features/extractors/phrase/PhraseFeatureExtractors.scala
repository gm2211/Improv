package cbr.description.features.extractors.phrase

import cbr.description.features.extractors.SingleFeatureExtractor
import representation.Phrase

object PhraseFeatureExtractors {
  def getDefault = {
    new CompositePhraseFeatureExtractor(getDefaultSimpleExtractors)
  }

  def getDefaultSimpleExtractors: List[SingleFeatureExtractor[Phrase]] = {
    List(
      new AverageNoteDurationExtractor,
      new AverageMelodicIntervalExtractor,
      new AverageStartTimeIntervalExtractor,
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
      new VarianceOfNoteDurationExtractor,
      new VarianceOfStartTimeIntervals,
      new TempoExtractor,
      new KeyExtractor,
      new BasicPitchHistogramExtractor
    )
  }

  def getJSymbolicFeatureExtractor = JSymbolicPhraseWeightedFeatureExtractor.getDefault

}
