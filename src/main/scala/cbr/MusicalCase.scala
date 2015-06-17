package cbr

import instruments.InstrumentType.InstrumentType
import representation.{Jazz, MusicGenre, Phrase}

case class MusicalCase(
  instrumentType: InstrumentType,
  genre: MusicGenre = Jazz,
  phrase: Phrase)
