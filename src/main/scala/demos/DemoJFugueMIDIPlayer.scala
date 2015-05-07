package demos

import java.io.File

object DemoJFugueMIDIPlayer {
  def run(filename: String) = {
    import org.jfugue.midi.MidiFileManager
    import org.jfugue.player.Player
    val player = new Player()
    val pattern = MidiFileManager.loadPatternFromMidi(new File(filename))
    println(pattern.toString)
    player.play(pattern)
  }


}
