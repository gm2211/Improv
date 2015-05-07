package tests.midi

import midi.JFugueMIDIParser
import org.scalatest.FlatSpec

class JFugueMIDIParserTest extends FlatSpec {
  "The parser" should "return the correct parts by instrument type" in {
    val filename = getClass.getClassLoader.getResource("musicScores/midi_export.mid").getPath
    JFugueMIDIParser(filename)
  }

}
