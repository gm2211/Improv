import demos.DemoMIDIOrchestra

object Main extends App {
  val filename = getClass.getClassLoader.getResource("musicScores/midi_export.mid").getPath
  //  val filename = getClass.getClassLoader.getResource("musicScores/shorterTest.mid").getPath
  DemoMIDIOrchestra.run(filename)
  //  DemoRandomOrchestra.run()
  //  JMusicMIDIPlayer.run(filename)
  //  JFugueMIDIPlayer.run(filename)
}















