package demos

import java.io.File

import instruments.JFugueUtils
import midi.JMusicMIDIParser
import org.jfugue.midi.MidiFileManager
import org.jfugue.player.Player

object DemoCompareParserOutput {
  def run(filename: String) = {
    val player = new Player()
    val pattern = MidiFileManager.loadPatternFromMidi(new File(filename)).toString.split("V[0-9]").tail
    val myPhrase = JMusicMIDIParser.apply(filename).getMultiVoicePhrases(1).toList.head
    val myPattern = JFugueUtils.createPattern(myPhrase, 0)
    println(pattern(0))
    println(myPattern)

    Thread.sleep(1000)
    player.play(myPattern)
  }
}
