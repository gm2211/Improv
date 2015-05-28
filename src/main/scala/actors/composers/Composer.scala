package actors.composers

import instruments.InstrumentType.InstrumentType
import representation.Phrase

trait Composer {
  def compose(phrasesByOthers: Traversable[(InstrumentType, Phrase)]): Option[Phrase]
}
