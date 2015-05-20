import utils.IOUtils

object Main extends App {
//  val filename = IOUtils.getResourcePath("musicScores/midi_export.mid")
  val filename = IOUtils.getResourcePath("musicScores/test.mid")
//  val filename = IOUtils.getResourcePath("musicScores/shorterTest.mid")
//    val filename = IOUtils.getResourcePath("musicScores/shortPiano.mid")
//  val filename = IOUtils.getResourcePath("musicScores/piano.mid")
//  demos.DemoCompareParserOutput.run(filename)
  demos.DemoMIDIOrchestra.run(filename)
  //    demos.DemoRandomOrchestra.run()
//      demos.DemoJMusicMIDIPlayer.run(filename)
//  demos.DemoJFugueMIDIPlayer.run(filename)
//  demos.DemoPopulateDB.run(filename)
}

