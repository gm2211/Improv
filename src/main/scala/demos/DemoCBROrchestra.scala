package demos

import actors.Orchestra
import actors.composers.CBRComposer
import actors.directors.SimpleDirector
import actors.musicians.AIMusician
import instruments.InstrumentType.InstrumentType
import instruments.JFugueInstrument
import midi.segmentation.PhraseSegmenter
import representation.Phrase
import storage.KDTreeIndex

import scala.concurrent.duration.{NANOSECONDS => NANOS}

object DemoCBROrchestra {
  def run(): Unit = {
    val syncFreq = NANOS.toMillis(PhraseSegmenter.DEFAULT_SUB_PHRASE_LENGTH_NS.toLong)
    val director = Option(SimpleDirector.builder.withSyncFrequencyMS(syncFreq))
    val orchestra = Orchestra.builder.withDirector(director).build

    val musicianBuilder = (instrType: InstrumentType, partNumber: Int) => {
      val instrument = new JFugueInstrument(instrType)
      val composer = new CBRComposer(KDTreeIndex.loadDefault[Phrase].get)

      AIMusician.builder
        .withInstrument(instrument)
        .withComposer(composer)
    }
  }
}
