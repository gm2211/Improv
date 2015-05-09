package demos

import midi.JMusicMIDIParser

object DemoJMusicMIDIPlayer {
  def run(filename: String): Unit = {
    import jm.util.Play
    val parser = JMusicMIDIParser(filename)
    Play.midi(parser.score)
  }
}
