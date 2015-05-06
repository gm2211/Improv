package tests

import _root_.instruments.InstrumentType.PIANO
import midi.JMusicMIDIParser
import org.scalatest.FlatSpec

class JMusicMIDIParserTest extends FlatSpec {
  val resourcePath = getClass.getClassLoader.getResource("musicScores/midi_export.mid").getPath
  val parser = JMusicMIDIParser(resourcePath)

  "The midi parser" should "return a list of parts and their indexes grouped by instrument" in {
    val partsIndices = parser.getPartIndexByInstrument.get(PIANO(1)).get
    require(partsIndices == Set(0, 1))
  }
}
