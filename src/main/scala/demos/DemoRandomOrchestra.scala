package demos

import actors.Orchestra
import actors.directors.SimpleDirector
import actors.musicians.AIMusician
import instruments.InstrumentType._
import instruments.JFugueInstrument
import utils.ImplicitConversions.wrapInOption

object DemoRandomOrchestra {
  def run() = {
    val director = Option(SimpleDirector.builder.withSyncFrequencyMS(3000L))
    val orchestra = Orchestra.builder.withDirector(director).build
    val instrSet = Set(PIANO(), BRASS(), PERCUSSIVE(), CHROMATIC_PERCUSSION())

    val musicianBuilder = (instrType: InstrumentType) => {
      val instrument = new JFugueInstrument(instrumentType = instrType)
      AIMusician.builder
        .withInstrument(instrument)
    }

    instrSet
      .map(t => musicianBuilder(t).withActorSystem(orchestra.system))
      .foreach(m => orchestra.registerMusician(m.build))

    orchestra.start()

  }
}
