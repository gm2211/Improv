package demos

import java.util.concurrent.TimeUnit

import instruments.JFugueUtils
import midi.{JMusicMIDIParser, MIDIPlayer}
import org.jfugue.player.Player
import training.DefaultMusicalCaseExtractor

object DemoANN {
  def run(filename: String) = {
    val extractor = new DefaultMusicalCaseExtractor(JMusicMIDIParser)

    val cases = extractor.getCases(filename)
    val player = new Player()
    var playing = false
    for ((_, sol) <- cases) {
      val pattern = JFugueUtils.createPattern(sol.phrase, sol.instrumentType)
      val seq = JFugueUtils.toSequence(sol.phrase, sol.instrumentType)
      new MIDIPlayer().play(seq)
//      println(ProfilingUtils.timeIt(player.play(pattern)))

      println(pattern)
      println(sol.phrase)
      println(sol.phrase.getDuration(TimeUnit.MILLISECONDS))
      val rating: Int = scala.io.StdIn.readLine("Rating: ", "%d").toInt
    }
  }

}
