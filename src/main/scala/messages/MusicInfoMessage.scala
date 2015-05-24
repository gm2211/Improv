package messages

import instruments.InstrumentType.InstrumentType
import representation.Phrase

case class MusicInfoMessage(
  phrase: Phrase,
  time: Long,
  instrument: InstrumentType) extends Message {
}
