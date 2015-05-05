package demos

import actors.Orchestra
import actors.composers.MIDIReaderComposer
import actors.directors.SimpleDirector
import actors.musicians.AIMusician
import instruments.InstrumentType._
import instruments.JFugueInstrument
import midi.JMusicMIDIParser
import utils.ImplicitConversions.wrapInOption

object DemoMIDIOrchestra {
  def run(filename: String) = {
    val director = Option(SimpleDirector.builder.withSyncFrequencyMS(5000L))
    val orchestra = Orchestra.builder.withDirector(director).build
    val parser = JMusicMIDIParser(filename)

    val musicianBuilder = (instrType: InstrumentType, partNumber: Int) => {
      val instrument = new JFugueInstrument(instrType)
      AIMusician.builder
        .withInstrument(instrument)
        .withComposer(new MIDIReaderComposer(filename, partNumber))
    }

    for ((instrument, parts) <- parser.getPartIndexByInstrument) {
      parts.map (musicianBuilder (instrument, _).withActorSystem (orchestra.system) )
           .foreach (m => orchestra.registerMusician (m.build) )
      }

    orchestra.start()
  }
}
