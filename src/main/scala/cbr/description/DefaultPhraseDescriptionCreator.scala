package cbr.description

import cbr.description.features.extractors.WeightedFeatureExtractor
import cbr.description.features.extractors.phrase.PhraseFeatureExtractors
import instruments.InstrumentType.InstrumentType
import representation.Phrase

class DefaultPhraseDescriptionCreator extends DescriptionCreator[(InstrumentType, Phrase)] {
    override val featureExtractor: WeightedFeatureExtractor[(InstrumentType, Phrase)] = PhraseFeatureExtractors.getDefault
}
