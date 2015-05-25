package cbr.description

import cbr.description.features.extractors.WeightedFeatureExtractor
import cbr.description.features.extractors.phrase.PhraseFeatureExtractors
import representation.Phrase

class DefaultPhraseDescriptionCreator extends DescriptionCreator[Phrase] {
    override val featureExtractor: WeightedFeatureExtractor[Phrase] = PhraseFeatureExtractors.getDefault
}
