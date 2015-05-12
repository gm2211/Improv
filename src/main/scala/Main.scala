object Main extends App {
//    val filename = getClass.getClassLoader.getResource("musicScores/midi_export.mid").getPath
  val filename = getClass.getClassLoader.getResource("musicScores/test.mid").getPath
//  val filename = getClass.getClassLoader.getResource("musicScores/pianoShort.mid").getPath
//  DemoCompareParserOutput.run(filename)
  demos.DemoMIDIOrchestra.run(filename)
  //    demos.DemoRandomOrchestra.run()
//      demos.DemoJMusicMIDIPlayer.run(filename)
//  demos.DemoJFugueMIDIPlayer.run(filename)
}

















