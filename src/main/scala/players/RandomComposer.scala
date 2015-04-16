package players

import representation.{Note, Phrase}

class RandomComposer extends Composer {
  override def compose(previousPhrase: Phrase): Phrase = {
    Phrase.builder
      .withMusicalElements((1 to 10).map(i => Note.genRandNote()).toList)
      .build()
  }
}
