package instruments

import representation.MusicalElement

trait Instrument {
  def play(musicalElemtn: MusicalElement): Unit
}
