
object Main extends App {
  val filename = getClass.getClassLoader.getResource("musicScores/midi_export.mid").getPath
  //  val filename = getClass.getClassLoader.getResource("musicScores/shorterTest.mid").getPath
//  demos.DemoMIDIOrchestra.run(filename)
  //  demos.DemoRandomOrchestra.run()
    demos.DemoJMusicMIDIPlayer.run(filename)
//    demos.DemoJFugueMIDIPlayer.run(filename)
}















