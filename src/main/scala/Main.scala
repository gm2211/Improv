

object Main extends App {
  //  val filename = IOUtils.getResourcePath("musicScores/midi_export.mid")
  //  val filename = IOUtils.getResourcePath("musicScores/shorterTest.mid")
  //    val filename = IOUtils.getResourcePath("musicScores/shortPiano.mid")
  //  val filename = IOUtils.getResourcePath("musicScores/piano.mid")
  //  val filename = IOUtils.getResourcePath("trainingMIDIs/myTrainingExample.mid")
  //  demos.DemoCompareParserOutput.run(filename)
  //  demos.DemoMIDIOrchestra.run(filename)
  //    demos.DemoRandomOrchestra.run()
  //      demos.DemoJMusicMIDIPlayer.run(filename)
  //  demos.DemoJFugueMIDIPlayer.run(filename)
  val trainingDir = "trainingMIDIs"
      demos.DemoPopulateDB.run(trainingDir)
//      demos.DemoCBROrchestra.run()
//  val index = KDTreeIndex.loadDefault[(InstrumentType, Phrase)].get
//  index.foreach(cd => println(index.findSolutionsToSimilarProblems(cd, 1).head._1))
}

