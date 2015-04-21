package messages

import representation.MusicalElement

case class MusicInfoMessage(musicalElement: MusicalElement, time: Long) extends Message {
}
