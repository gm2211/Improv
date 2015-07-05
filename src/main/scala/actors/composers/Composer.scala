package actors.composers

import cbr.MusicalCase
import instruments.InstrumentType.InstrumentType
import representation.Phrase

trait Composer {
  def compose(
    previousSolution: MusicalCase,
    phrasesByOthers: Traversable[MusicalCase],
    constraints: List[MusicalCase => Boolean]): Option[Phrase]
}
