package demos

import java.util.concurrent.TimeUnit
import javax.sound.midi.{MetaMessage, MetaEventListener, MidiSystem}

import instruments.JFugueUtils
import jm.util.Play
import midi.{MIDIConverter, JMusicConverterUtils, JMusicMIDIParser}
import org.jfugue.player.Player
import training.DefaultMusicalCaseExtractor
import utils.ProfilingUtils

object DemoANN {
  def run(filename: String) = {
    val extractor = new DefaultMusicalCaseExtractor(JMusicMIDIParser)

    val cases = extractor.getCases(filename)
    val player = new Player()
    for ((_, sol) <- cases) {
      val pattern = JFugueUtils.createPattern(sol.phrase, sol.instrumentType)
      println(ProfilingUtils.timeIt(player.play(pattern)))
      println(pattern)
      println(sol.phrase)
      println(sol.phrase.getDuration(TimeUnit.MILLISECONDS))
      val rating: Int = scala.io.StdIn.readLine("Rating: ", "%d").toInt
    }
  }

}
