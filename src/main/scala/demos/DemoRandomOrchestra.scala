package demos

import actors.Orchestra
import actors.directors.SimpleDirector
import actors.musicians.AIMusician
import actors.musicians.behaviour.{SyncMessageReceivedBehaviour, MusicMessageInfoReceivedBehaviour}
import instruments.InstrumentType._
import instruments.JFugueInstrument

object DemoRandomOrchestra {
  def run() = {
    val director = SimpleDirector.builder.withSyncFrequencyMS(3000L)
    val orchestra = Orchestra.builder.withDirector(director).build
    val instrSet = Set(PIANO(), BRASS(), PERCUSSIVE(), CHROMATIC_PERCUSSION())

    val musicianBuilder = (instrType: InstrumentType) => {
      val instrument = new JFugueInstrument(instrumentType = instrType)
      AIMusician.builder
        .withInstrument(instrument)
        .addBehaviour(new MusicMessageInfoReceivedBehaviour)
        .addBehaviour(new SyncMessageReceivedBehaviour)
    }

    instrSet
      .map(t => musicianBuilder(t).withActorSystem(orchestra.system))
      .foreach(m => orchestra.registerMusician(m.build))

    orchestra.start()

  }
}
