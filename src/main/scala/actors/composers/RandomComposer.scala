package actors.composers

import representation.{Note, Phrase}
import utils.CollectionUtils

class RandomComposer extends Composer {
  override def compose(previousPhrase: Phrase): Phrase = {
    val range = CollectionUtils.randomRange(1, 8)
    Phrase.builder
      .withMusicalElements(range.map(i => Note.genRandNote()).toList)
      .build()
  }
}
