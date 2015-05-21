package messages

import instruments.InstrumentType.InstrumentType
import representation.{Phrase, MusicalElement}

case class MusicInfoMessage(
  phrase: Phrase,
  time: Long,
  instrument: InstrumentType) extends Message {
}
