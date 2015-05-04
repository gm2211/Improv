package tests.instruments

import tests.TestTags
import TestTags.SlowTest
import tests.instruments.InstrumentType.PIANO
import org.scalatest.FlatSpec
import representation.{Rest, Note, Phrase}

class JFugueInstrumentTest extends FlatSpec {
  var instrument: JFugueInstrument = _
  def setup() = {
    instrument = new JFugueInstrument(PIANO())
  }

  "An instrument" should "play a phrase note by note" taggedAs SlowTest in {
    setup()
    val phrase = Phrase.builder
      .addMusicalElement(Note.fromString("A5").withDuration(3))
      .addMusicalElement(new Rest(2000))
      .addMusicalElement(Note.fromString("B5").withDuration(3))
      .addMusicalElement(new Rest(2000))
      .addMusicalElement(Note.fromString("C5").withDuration(3))
      .build()

    instrument.play(phrase)
    Thread.sleep(10000)

  }

}
