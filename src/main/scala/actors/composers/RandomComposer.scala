package actors.composers

import representation.{Note, Phrase}

class RandomComposer extends Composer {
  override def compose(previousPhrase: Phrase): Option[Phrase] = {
    val range = 1 to 5
    val phrase = Phrase()
      .withMusicalElements(range.map(i => Note.genRandNote().withDuration(0.5)))
    Some(Phrase())
  }
}
