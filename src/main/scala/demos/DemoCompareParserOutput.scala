package demos

import java.io.File

import instruments.JFugueUtils
import midi.JMusicMIDIParser
import org.jfugue.midi.MidiFileManager
import org.jfugue.player.Player

object DemoCompareParserOutput {
  def run(filename: String) = {
    val pattern = MidiFileManager.loadPatternFromMidi(new File(filename)).toString.split("V[0-9]").tail
    val myPhrases = JMusicMIDIParser.apply(filename).getMultiVoicePhrases(0)
    val myPatterns = myPhrases.map(JFugueUtils.createPattern(_, 0))
    println(pattern(0))
    myPatterns.foreach(println)

    new Player().play(myPatterns.head)
//    new Player().play(pattern(0))
  }
}
