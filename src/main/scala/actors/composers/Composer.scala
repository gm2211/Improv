package actors.composers

import representation.Phrase

trait Composer {
  def compose(phrasesByOthers: List[Phrase]): Option[Phrase]
}
