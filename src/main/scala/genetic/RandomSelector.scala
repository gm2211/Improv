package genetic

import instruments.InstrumentType.InstrumentType
import representation.Phrase
import utils.ImplicitConversions.toEnhancedIterable
import utils.collections.CollectionUtils

class RandomSelector extends PopulationSelector[(InstrumentType, Phrase)] {
  override def selectSolution(
      candidates: Traversable[(InstrumentType, Phrase)],
      constraints: Traversable[((InstrumentType, Phrase)) => Boolean]): Option[(InstrumentType, Phrase)] = {
    CollectionUtils.chooseRandom(candidates.filter(constraints))
  }
}
