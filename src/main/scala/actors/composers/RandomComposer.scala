package actors.composers

import instruments.InstrumentType.InstrumentType
import representation.{Note, Phrase}

class RandomComposer extends Composer {
  override def compose(phrasesByOthers: Traversable[(InstrumentType, Phrase)]): Option[Phrase] = {
    val range = 1 to 5
    val phrase = Phrase()
      .withMusicalElements(range.map(i => Note.genRandNote().withDurationBPM(0.5, 120.0)))
    Some(phrase)
  }
}
