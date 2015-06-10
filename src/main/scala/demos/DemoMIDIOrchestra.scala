package demos

import actors.Orchestra
import actors.composers.{CBRComposer, MIDIReaderComposer, RandomComposer}
import actors.directors.WaitingDirector
import actors.musicians.AIMusician
import cbr.MusicalCase
import instruments.InstrumentType._
import instruments.JFugueInstrument
import midi.JMusicMIDIParser
import storage.KDTreeIndex

object DemoMIDIOrchestra {
  private def createComposer(filename: String, partNumber: Int) = {
    val composer = "midi"
    composer match {
      case "rand" =>
        new RandomComposer
      case "midi" =>
        MIDIReaderComposer.builder
          .withFilename(filename)
          .withPartNum(partNumber)
          .withMIDIParser(JMusicMIDIParser)
          .build
      case "cbr" =>
        val index = KDTreeIndex.loadDefault[MusicalCase].get
        new CBRComposer(index)
    }
  }

  def run(filename: String) = {
    val orchestra = Orchestra.builder
      .withDirector(WaitingDirector.builder)
      .build
    val parser = JMusicMIDIParser(filename)

    val musicianBuilder = (instrType: InstrumentType, partNumber: Int) => {
      val instrument = new JFugueInstrument(instrType)
      val composer = createComposer(filename, partNumber)

      AIMusician.builder
        .withInstrument(instrument)
        .withBehaviours(AIMusician.getDefaultBehaviours)
        .withComposer(composer)
//        .isMessageOnly
    }

    for (((instrument, parts), idx) <- parser.getPartIndexByInstrument.zipWithIndex if idx < 2) {
      parts.map(musicianBuilder(instrument, _)
        .withActorSystem(orchestra.system))
        .foreach(m => orchestra.registerMusician(m.build))
    }

//        orchestra.registerMusician(new JFugueSynchronizedPlayer)

    orchestra.start()
  }
}
