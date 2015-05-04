package instruments

import designPatterns.observer.{EventNotification, Observable}
import instruments.InstrumentType.{CHROMATIC_PERCUSSION, PERCUSSIVE, InstrumentType, PIANO}
import org.jfugue.async.Listener
import org.jfugue.pattern.Pattern
import org.jfugue.player.Player
import org.jfugue.player.PlayerEvents.FINISHED_PLAYING
import org.jfugue.{async, theory}
import org.slf4j.LoggerFactory
import representation.{MusicalElement, Note, Phrase, Rest}
import utils.ImplicitConversions.anyToRunnable

class JFugueInstrument(override val instrumentType: InstrumentType = PIANO()) extends Instrument with Observable with Listener {
  private val log = LoggerFactory.getLogger(getClass)
  private var _finishedPlaying = true
  def finishedPlaying = _finishedPlaying


  override def play(musicalElement: MusicalElement): Unit = {
    if (! _finishedPlaying) {
      log.debug("Still busy. Ignoring..")
    } else {
      val musicPattern: Pattern = JFugueUtils.createPattern(musicalElement, instrumentType.instrumentNumber)
//      _finishedPlaying = false
      playWithPlayer(musicPattern.toString)
    }
  }

  private def playWithPlayer(pattern: String): Unit = {
    new Thread(() => {
      val player = new Player()
      player.play(pattern)
    }).start()
  }

  override def notify(eventNotification: async.EventNotification): Unit = eventNotification match {
    case FINISHED_PLAYING =>
      _finishedPlaying = true
      notifyObservers(FinishedPlaying)

  }
}

case object FinishedPlaying extends EventNotification

object JFugueUtils {

  def createPattern(element: MusicalElement): Pattern = element match {
    case note: Note =>
      convertNote(note).getPattern
    case rest: Rest =>
      theory.Note.createRest(rest.durationSec).getPattern
    case phrase: Phrase =>
      new Pattern(phrase.map(element => s"${createPattern(element).getPattern.toString}").mkString(" "))
  }

  def createPattern(musicalElement: MusicalElement, instrumentNumber: Int): Pattern = {
    val pattern: Pattern = createPattern(musicalElement)
    pattern.setInstrument(instrumentNumber)
    if (PERCUSSIVE.range.contains(instrumentNumber) || CHROMATIC_PERCUSSION.range.contains(instrumentNumber)) {
      pattern.setVoice(9)
    }
    pattern
  }

  def convertNote(note: Note): theory.Note =
    new theory.Note(s"${note.name.toString}").setDuration(note.duration/5)

  def convertNote(note: Note, instrumentNumber: Int): Pattern = {
    val convertedNotePattern = convertNote(note).getPattern
    convertedNotePattern.setInstrument(instrumentNumber)
    convertedNotePattern
  }
}
