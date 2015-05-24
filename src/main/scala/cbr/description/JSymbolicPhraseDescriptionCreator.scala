package cbr.description

import cbr.description.features.FeatureExtractor
import representation.Phrase

class JSymbolicPhraseDescriptionCreator extends DescriptionCreator[Phrase] {
    override val featureExtractor: FeatureExtractor[Phrase] = FeatureExtractor.getDefaultForPhrase
}
