package demos

import java.io.File

import instruments.JFugueUtils
import midi.JMusicMIDIParser
import org.jfugue.midi.MidiFileManager
import org.jfugue.player.Player

object DemoCompareParserOutput {
  def run(filename: String) = {
    import scala.concurrent.duration.MILLISECONDS
    val pattern = MidiFileManager.loadPatternFromMidi(new File(filename)).toString.split("V[0-9]").tail
    val myPhrases = JMusicMIDIParser.apply(filename).getMultiVoicePhrases(1)
    val myPatterns = myPhrases.map(JFugueUtils.createPattern(_, 0))
    println(pattern(0))
    myPhrases.foreach(p => println(p.getDuration(MILLISECONDS)))
    myPatterns.foreach(println)

    myPatterns.foreach{ p => new Player().play(p); println("done");Thread.sleep(5000)}
//    new Player().play(pattern(0))
  }
}
