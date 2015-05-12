import java.io.File

import instruments.JFugueUtils
import midi.JMusicMIDIParser
import org.jfugue.midi.MidiFileManager
import org.jfugue.player.Player

object Main extends App {
//    val filename = getClass.getClassLoader.getResource("musicScores/midi_export.mid").getPath
//  val filename = getClass.getClassLoader.getResource("musicScores/shorterTest.mid").getPath
  val filename = getClass.getClassLoader.getResource("musicScores/pianoShort.mid").getPath
  DemoCompareParserOutput.run(filename)
//  demos.DemoMIDIOrchestra.run(filename)
  //    demos.DemoRandomOrchestra.run()
//      demos.DemoJMusicMIDIPlayer.run(filename)
//  demos.DemoJFugueMIDIPlayer.run(filename)
}

object DemoCompareParserOutput {
  def run(filename: String) = {
    val player = new Player()
    val pattern = MidiFileManager.loadPatternFromMidi(new File(filename)).toString.split("V[0-9]").tail
    val myPattern = JFugueUtils.createPattern(JMusicMIDIParser.apply(filename).getMultiVoicePhrases(0).toList.head, 0)
    println(pattern(0))
    println(myPattern)

    player.play(pattern(0))
    Thread.sleep(2000)
    player.play(myPattern)
//    player.play(myPattern)
  }
}















