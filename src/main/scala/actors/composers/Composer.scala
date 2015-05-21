package actors.composers

import representation.Phrase

trait Composer {
  def compose(phrasesByOthers: Traversable[Phrase]): Option[Phrase]
}
