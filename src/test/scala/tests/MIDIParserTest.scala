package tests

import midi.MIDIParser
import org.scalatest.FlatSpec

class MIDIParserTest extends FlatSpec {
  val resourcePath = getClass.getClassLoader.getResource("musicScores/test.mid").getPath
  val parser = MIDIParser(resourcePath)

  "The midi parser" should "return a list of parts and their indexes grouped by instrument" in {
    //TODO: Write this test
  }
}
