package actors.composers

import representation.{Note, Phrase}

class RandomComposer extends Composer {
  override def compose(phrasesByOthers: Traversable[Phrase]): Option[Phrase] = {
    val range = 1 to 5
    val phrase = Phrase()
      .withMusicalElements(range.map(i => Note.genRandNote().withDurationBPM(0.5, 120.0)))
    Some(phrase)
  }
}
