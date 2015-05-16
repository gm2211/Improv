package tests

import _root_.instruments.InstrumentType.PIANO
import _root_.midi.{JMusicMIDIParser, JMusicParserUtils}
import _root_.utils.ProfilingUtils
import _root_.utils.ImplicitConversions.toEnhancedTraversable
import org.scalatest.FlatSpec
import representation.Phrase

class JMusicMIDIParserTest extends FlatSpec {
  val resourcePath = getClass.getClassLoader.getResource("musicScores/test.mid").getPath
  val parser = JMusicMIDIParser(resourcePath)

  "The midi parser" should "return a list of parts and their indexes grouped by instrument" in {
    val partsIndices = parser.getPartIndexByInstrument.get(PIANO(1)).get
    assert(partsIndices == Set(0, 1))
  }

  "The midi parser" should "correctly assemble a phrase" in {
    println()
//    val multiVoicePhrase = Phrase()
//      .withMusicalElements(Phrase().withMusicalElements(Rest(startTime = 0, duration = 0.5), Note(name = E, duration = 0.5, startTime = 1)),
//                           Phrase().withMusicalElements(Note(name = B, startTime = 0, duration = 0.5), Note(name = D, startTime = 1)),
//                           Phrase().withMusicalElements(Note(name = C, startTime = 0, duration = 0.5), Rest(startTime = 1)))
//      .withPolyphony(polyphony = true).get
    val multiVoicePhrase = JMusicParserUtils.convertPart(parser.score.getPart(1))
    val mergedPhrase = JMusicParserUtils.mergePhrases(multiVoicePhrase).getOrElse(Phrase())
    val splitTask = () => JMusicParserUtils.splitPhrase(mergedPhrase)

    val (timeElapsed, multiVoicePhraseRecovered) = ProfilingUtils.timeIt(splitTask(), 1)
    println(s"\nOn average it took $timeElapsed milliseconds to get $multiVoicePhraseRecovered")
    List(multiVoicePhrase.musicalElements.head.asInstanceOf[Phrase].musicalElements.sortBy(_.getStartTime).take(10), multiVoicePhraseRecovered.musicalElements.head.asInstanceOf[Phrase].musicalElements.sortBy(_.getStartTime).take(10)).zipped.foreach(println)
  }
}
