package demos

import actors.Orchestra
import actors.composers.CBRComposer
import actors.directors.WaitingDirector
import actors.musicians.AIMusician
import actors.musicians.AIMusician._
import cbr.MusicalCase
import genetic.PhraseSelectors
import instruments.JFugueInstrument
import storage.KDTreeIndex

object DemoCBROrchestra extends App{
  run()

  def run(): Unit = {
    val director = WaitingDirector.builder
    val orchestra = Orchestra.builder.withDirector(director).build
    val numMusicians = 15

    def musicianBuilder = {
      val composer = new CBRComposer(KDTreeIndex.loadDefault[MusicalCase].get, Some(PhraseSelectors.getGASelector))
      val instrument = new JFugueInstrument

      AIMusician.builder
        .withInstrument(instrument)
        .withComposer(composer)
    }

    (1 to numMusicians).foreach(_ =>orchestra.registerMusician(musicianBuilder))
    orchestra.start()
  }
}
