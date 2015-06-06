import javax.sound.midi.{MetaMessage, MetaEventListener, MidiSystem, Sequencer}

import instruments.InstrumentType.PIANO
import instruments.JFugueUtils
import midi.JMusicMIDIParser
import org.jfugue.player.Player
import utils.IOUtils
import utils.collections.CollectionUtils
import utils.ImplicitConversions.anyToRunnable

object Main extends App {
  //  val filename = IOUtils.getResourcePath("musicScores/midi_export.mid")
//    val filename = IOUtils.getResourcePath("musicScores/shorterTest.mid")
  //    val filename = IOUtils.getResourcePath("musicScores/shortPiano.mid")
  //  val filename = IOUtils.getResourcePath("musicScores/piano.mid")
  val filename = IOUtils.getResourcePath("musicScores/test.mid")
  //  val filename = IOUtils.getResourcePath("trainingMIDIs/myTrainingExample.mid")
  //  demos.DemoCompareParserOutput.run(filename)
    demos.DemoMIDIOrchestra.run(filename)
  //    demos.DemoRandomOrchestra.run()
  //      demos.DemoJMusicMIDIPlayer.run(filename)
  //  demos.DemoJFugueMIDIPlayer.run(filename)
  val trainingDir = "trainingMIDIs"
//      demos.DemoPopulateDB.run(trainingDir)
//      demos.DemoCBROrchestra.run()

//  def runnable: Runnable = () => {
//    val filename = IOUtils.getResourcePath("musicScores/test.mid")
//    val phrase = CollectionUtils.chooseRandom(JMusicMIDIParser(filename).getMultiVoicePhrases(1)).get
//    new Player().play(JFugueUtils.createPattern(phrase, PIANO(1)))
//  }
//
//  (1 to 10).foreach(i => new Thread(runnable).start())
}

