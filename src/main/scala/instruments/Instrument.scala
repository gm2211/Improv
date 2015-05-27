package instruments

import instruments.InstrumentType.InstrumentType
import representation.Phrase

trait Instrument {
  def play(phrase: Phrase): Unit

  val instrumentType: InstrumentType
}
