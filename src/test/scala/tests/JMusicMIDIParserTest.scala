package tests

import midi.{JMusicMIDIParser, JMusicMIDIParser$}
import org.scalatest.FlatSpec

class JMusicMIDIParserTest extends FlatSpec {
  val resourcePath = getClass.getClassLoader.getResource("musicScores/test.mid").getPath
  val parser = JMusicMIDIParser(resourcePath)

  "The midi parser" should "return a list of parts and their indexes grouped by instrument" in {
    //TODO: Write this test
  }
}
