package tests.instruments

import tests.instruments.InstrumentType.{InstrumentType, PIANO}
import org.jfugue.player.{PlayerListener, Player}
import org.jfugue.theory
import org.slf4j.LoggerFactory
import representation.{MusicalElement, Note, Phrase, Rest}
import tests.utils.ImplicitConversions.anyToRunnable

class JFugueInstrument(override val instrumentType: InstrumentType = PIANO()) extends Instrument with PlayerListener {
  private val log = LoggerFactory.getLogger(getClass)
  val player = new Player
  var finishedPlaying = true

  player.addListener(this)

  override def play(musicalElement: MusicalElement): Unit = {
    if (! finishedPlaying) {
      log.debug("Still playing old music. Ignoring..")
      return
    }
    musicalElement match {
      case note: Note =>
        val convertedNotePattern = JFugueUtils.convertNote(note).getPattern
        convertedNotePattern.setInstrument(instrumentType.instrumentNumber)
        finishedPlaying = false
        new Thread(() => player.play(convertedNotePattern)).start()
      case r: Rest =>
        finishedPlaying = false
        new Thread(() => {Thread.sleep(r.durationMS.toInt); onFinished()}).start()
      case p: Phrase =>
        new Thread(() => p.foreach(play)).start()
    }
  }


  override def onFinished(): Unit = {
    finishedPlaying = true
  }
}

object JFugueUtils {
  def convertNote(note: Note): theory.Note =
    new theory.Note(s"${note.name.toString}").setDuration(note.duration/5)
}
