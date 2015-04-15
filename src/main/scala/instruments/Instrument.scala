package instruments

import representation.Note

trait Instrument {
  def play(note: Note): Unit
}
