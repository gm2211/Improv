package cbr.description

import cbr.MusicalCase
import cbr.description.features.extractors.WeightedFeatureExtractor
import cbr.description.features.extractors.phrase.PhraseFeatureExtractors

class DefaultPhraseDescriptionCreator extends DescriptionCreator[MusicalCase] {
    override val featureExtractor: WeightedFeatureExtractor[MusicalCase] = PhraseFeatureExtractors.getDefault
}
