package cbr

import instruments.InstrumentType.InstrumentType
import representation.Phrase

case class MusicalCase(
  instrumentType: InstrumentType,
  phrase: Phrase)
