package demos

import midi.JMusicMIDIParser

object DemoJMusicMIDIPlayer {
  def run(filename: String): Unit = {
    import jm.util.Play
    val score = JMusicMIDIParser(filename).score
    println(score.getTempo)
    Play.midi(score)
  }
}
