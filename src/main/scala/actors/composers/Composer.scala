package actors.composers

import representation.Phrase

trait Composer {
  def compose(previousPhrase: Phrase): Option[Phrase]
}
