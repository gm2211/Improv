package actors.composers

import cbr.MusicalCase
import instruments.InstrumentType.InstrumentType
import representation.Phrase

trait Composer {
  def compose(phrasesByOthers: Traversable[MusicalCase], targetInstrument: InstrumentType): Option[Phrase]
}
