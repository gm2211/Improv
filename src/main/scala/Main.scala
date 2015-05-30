import utils.IOUtils

object Main extends App {
  //  val filename = IOUtils.getResourcePath("musicScores/midi_export.mid")
    val filename = IOUtils.getResourcePath("musicScores/shorterTest.mid")
  //    val filename = IOUtils.getResourcePath("musicScores/shortPiano.mid")
  //  val filename = IOUtils.getResourcePath("musicScores/piano.mid")
  //  val filename = IOUtils.getResourcePath("trainingMIDIs/myTrainingExample.mid")
  //  demos.DemoCompareParserOutput.run(filename)
    demos.DemoMIDIOrchestra.run(filename)
  //    demos.DemoRandomOrchestra.run()
  //      demos.DemoJMusicMIDIPlayer.run(filename)
  //  demos.DemoJFugueMIDIPlayer.run(filename)
  val trainingDir = "trainingMIDIs"
//      demos.DemoPopulateDB.run(trainingDir)
//      demos.DemoCBROrchestra.run()
//  val index = KDTreeIndex.loadDefault[MusicalCase].get
//  index.foreach(cd => println(index.findSolutionsToSimilarProblems(cd, 1).head.instrumentType))
//  val store = new MapDBSolutionStore[MusicalCase](IOUtils.getResourcePath("knowledgeBase/solutionStore"))
////  println(store.addSolution(MusicalCase(BRASS(57), Phrase())))
////  println(store.addSolution(MusicalCase(PIANO(1), Phrase())))
////  store.commit()
//  println(store.getSolution("ff82ef93-f9fc-44e3-9779-fc77f64e3ce6"))
//  println(store.getSolution("6f66d7e5-29f2-43f3-9cc4-60da49edb3c0"))
}

