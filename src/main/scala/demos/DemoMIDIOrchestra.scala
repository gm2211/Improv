package demos

import actors.Orchestra
import actors.composers.{CBRComposer, MIDIReaderComposer}
import actors.directors.SimpleDirector
import actors.musicians.AIMusician
import instruments.InstrumentType._
import instruments.JFugueInstrument
import midi.JMusicMIDIParser
import representation.Phrase
import storage.KDTreeIndex

object DemoMIDIOrchestra {
  private def createComposer(filename: String, partNumber: Int) = {
    val composer = "cbr"
    composer match {
      case "midi" =>
        MIDIReaderComposer.builder
          .withFilename(filename)
          .withPartNum(partNumber)
          .withMIDIParser(JMusicMIDIParser)
          .build
      case "cbr" =>
        val index = KDTreeIndex.loadDefault[(InstrumentType, Phrase)].get
        new CBRComposer(index)
    }
  }

  def run(filename: String) = {
    val director = Option(SimpleDirector.builder.withSyncFrequencyMS(6000))
    val orchestra = Orchestra.builder.withDirector(director).build
    val parser = JMusicMIDIParser(filename)

    val musicianBuilder = (instrType: InstrumentType, partNumber: Int) => {
      val instrument = new JFugueInstrument(instrType)
      val composer = createComposer(filename, partNumber)

      AIMusician.builder
        .withInstrument(instrument)
        .withComposer(composer)
      //        .isMessageOnly
    }

    for ((instrument, parts) <- parser.getPartIndexByInstrument.toStream) {
      parts.map(musicianBuilder(instrument, _)
        .withActorSystem(orchestra.system))
        .foreach(m => orchestra.registerMusician(m.build))
    }

    //    orchestra.registerMusician(new JFugueSynchronizedPlayer)

    orchestra.start()
  }
}
