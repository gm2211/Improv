package tests

import _root_.instruments.InstrumentType.PIANO
import _root_.midi.{JMusicParserUtils, JMusicMIDIParser}
import org.scalatest.FlatSpec
import tests.testutils.ProfilingUtils

class JMusicMIDIParserTest extends FlatSpec {
  val resourcePath = getClass.getClassLoader.getResource("musicScores/midi_export.mid").getPath
  val parser = JMusicMIDIParser(resourcePath)

  "The midi parser" should "return a list of parts and their indexes grouped by instrument" in {
    val partsIndices = parser.getPartIndexByInstrument.get(PIANO(1)).get
    assert(partsIndices == Set(0, 1))
  }

  "The midi parser" should "correctly assemble a phrase" in {
    println()
    val (timeElapsed, notesByStartTime) =
      ProfilingUtils.timeIt(JMusicParserUtils.getNotesByStartTime(parser.score.getPart(0).getPhrase(0)), 1)
    println(s"\nOn average it took $timeElapsed milliseconds to get $notesByStartTime")
  }
}
