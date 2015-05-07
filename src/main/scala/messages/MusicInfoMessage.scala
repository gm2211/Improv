package messages

import instruments.InstrumentType.InstrumentType
import representation.MusicalElement

case class MusicInfoMessage(
  musicalElement: MusicalElement,
  time: Long,
  instrument: InstrumentType) extends Message {
}
