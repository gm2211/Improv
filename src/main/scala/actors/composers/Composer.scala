package actors.composers

import cbr.MusicalCase
import representation.Phrase

trait Composer {
  def compose(phrasesByOthers: Traversable[MusicalCase]): Option[Phrase]
}
