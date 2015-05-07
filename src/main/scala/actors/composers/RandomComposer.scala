package actors.composers

import representation.{Note, Phrase}
import utils.collections.CollectionUtils

class RandomComposer extends Composer {
  override def compose(previousPhrase: Phrase): Option[Phrase] = {
    val range = 1 to 5//CollectionUtils.randomRange(1, 8)
    val phrase = Phrase.builder
      .withMusicalElements(range.map(i => Note.genRandNote().withDuration(0.5)).toList)
      .build
    Some(phrase)
  }
}
