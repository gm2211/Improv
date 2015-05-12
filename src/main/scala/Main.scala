import jm.util.Play
import midi.{JMusicConverterUtils, MIDIConverter, JMusicMIDIParser}

object Main extends App {
//    val filename = getClass.getClassLoader.getResource("musicScores/midi_export.mid").getPath
  val filename = getClass.getClassLoader.getResource("musicScores/shorterTest.mid").getPath
//  val filename = getClass.getClassLoader.getResource("musicScores/pianoShort.mid").getPath
//  DemoCompareParserOutput.run(filename)
  demos.DemoMIDIOrchestra.run(filename)
  //    demos.DemoRandomOrchestra.run()
//      demos.DemoJMusicMIDIPlayer.run(filename)
//  demos.DemoJFugueMIDIPlayer.run(filename)
  Thread.sleep(10000)
  val phrase = JMusicMIDIParser(filename).getMultiVoicePhrases(1).head
  println(phrase)
  Play.midi(JMusicConverterUtils.wrapInScore(JMusicConverterUtils.toPart(phrase)))
}

















