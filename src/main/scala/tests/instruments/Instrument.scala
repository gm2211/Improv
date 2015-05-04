package tests.instruments

import tests.instruments.InstrumentType.InstrumentType
import representation.MusicalElement

trait Instrument {
  def play(musicalElement: MusicalElement): Unit
  val instrumentType: InstrumentType
}
