package genetic

import instruments.InstrumentType.InstrumentType
import representation.Phrase
import utils.ImplicitConversions.toEnhancedIterable
import utils.collections.CollectionUtils

class RandomSelector[Elem] extends PopulationSelector[Elem] {
  override def selectSolution(
      candidates: Traversable[Elem],
      constraints: Traversable[(Elem) => Boolean]): Option[Elem] = {
    CollectionUtils.chooseRandom(candidates.filter(constraints))
  }
}
