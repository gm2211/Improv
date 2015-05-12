package instruments

import org.scalatest.FlatSpec
import representation.{Note, NoteName, Phrase}

class JFugueUtilsTest extends FlatSpec {
  
  "A polyphonic phrase" should "be converted into a pattern that uses @<time_val>" in {
    val phrases = List(
      List(new Note(NoteName.A, duration = 0.5), new Note(NoteName.D, duration = 1)),
      List(new Note(NoteName.B, duration = 0.5), new Note(NoteName.E, duration = 1)),
      List(new Note(NoteName.C, duration = 0.5), new Note(NoteName.F, duration = 1))
    ).map(nl => new Phrase(nl))
    val polyphonicPhrase = new Phrase(musicalElements = phrases.toList, polyphony = true)

    val pattern = JFugueUtils.createPattern(polyphonicPhrase, 0)
    println(pattern)
    assert(pattern.toString == "I[Piano] @0.0 A4h @0.0 B4h @0.0 C4h @0.5 D4w @0.5 E4w @0.5 F4w")
  }

}
