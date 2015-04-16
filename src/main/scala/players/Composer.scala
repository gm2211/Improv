package players

import representation.Phrase

trait Composer {
  def compose(previousPhrase: Phrase): Phrase
}
