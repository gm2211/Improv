package demos

import midi.JMusicMIDIParser

object DemoJMusicMIDIPlayer {
  def run(filename: String): Unit = {
    import jm.util.Play
    val parser = JMusicMIDIParser(filename)
    parser.getPhrase(0, 0)
    Play.midi(parser.score)
  }
}
