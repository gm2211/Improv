package cbr.description

import cbr.MusicalCase
import cbr.description.features.extractors.WeightedFeatureExtractor
import cbr.description.features.extractors.phrase.PhraseFeatureExtractors
import instruments.InstrumentType.InstrumentType
import representation.Phrase

class DefaultPhraseDescriptionCreator extends DescriptionCreator[MusicalCase] {
    override val featureExtractor: WeightedFeatureExtractor[MusicalCase] = PhraseFeatureExtractors.getDefault
}
