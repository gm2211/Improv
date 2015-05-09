package instruments

import java.util.concurrent.Executors

import designPatterns.observer.{EventNotification, Observable}
import instruments.InstrumentType.{CHROMATIC_PERCUSSION, InstrumentType, PERCUSSIVE, PIANO}
import org.jfugue.async.Listener
import org.jfugue.pattern.Pattern
import org.jfugue.player.Player
import org.jfugue.player.PlayerEvents.FINISHED_PLAYING
import org.jfugue.{async, theory}
import org.slf4j.LoggerFactory
import representation._
import utils.ImplicitConversions.anyToRunnable

class JFugueInstrument(override val instrumentType: InstrumentType = PIANO()) extends Instrument with Observable with Listener {
  private val log = LoggerFactory.getLogger(getClass)
  private val threadPool = Executors.newSingleThreadExecutor()
  private var _finishedPlaying = true

  def finishedPlaying = _finishedPlaying


  override def play(musicalElement: MusicalElement): Unit = {
    if (!_finishedPlaying) {
      log.debug("Still busy. Ignoring..")
      return
    }

    val musicPattern: Pattern = JFugueUtils.createPattern(musicalElement, instrumentType.instrumentNumber)
    log.debug(musicPattern.toString)
    _finishedPlaying = false
    playWithPlayer(musicPattern.toString)
  }

  private def playWithPlayer(pattern: String): Unit =
    threadPool.submit(() => {
      val player = new Player()
      player.addListener(this)
      player.play(pattern)
    })

  override def notify(eventNotification: async.EventNotification): Unit = {
    eventNotification match {
      case FINISHED_PLAYING =>
        _finishedPlaying = true
        notifyObservers(FinishedPlaying)
    }
  }
}

case object FinishedPlaying extends EventNotification

object JFugueUtils {
  val log = LoggerFactory.getLogger(getClass)

  /**
   * Takes a set of musical elements and generates a pattern that has a pattern corresponding to each on a different
   * pattern voice
   * e.g.: a set of {Phrase(...), Phrase(...), Note}
   * will result in
   * Pattern.voice(1) => <phrase_pattern>
   * Pattern.voice(2) => <phrase_pattern>
   * Pattern.voice(3) => <note_pattern>
   * @param musicalElements A set of musical elements
   * @return a Pattern
   */
  def createMultiVoicePattern(musicalElements: Set[(InstrumentType, MusicalElement)]) = {
    val pattern = new Pattern
    musicalElements.zipWithIndex.foreach { case ((instr, elem), idx) =>
      val voicePattern = createPattern(elem, instr.instrumentNumber)
      if (idx != 9) voicePattern.setVoice(idx)
      pattern.add(voicePattern)
    }
    pattern
  }

  def createPattern(musicalElement: MusicalElement, instrumentNumber: Int): Pattern =
    createPatternHelper(musicalElement, instrumentNumber).setInstrument(instrumentNumber)

  def createPatternHelper(musicalElement: MusicalElement, instrumentNumber: Int): Pattern = {
    val pattern: Pattern = musicalElement match {
      case note: Note =>
        convertNote(note, instrumentNumber).getPattern
      case rest: Rest =>
        theory.Note.createRest(rest.durationSec).getPattern
      case chord: Chord =>
        new Pattern(chord.notes.map(convertNote(_, instrumentNumber).getPattern.toString).mkString("+"))
      case phrase: Phrase =>
        new Pattern(phrase.map(createPatternHelper(_, instrumentNumber).toString).mkString(" "))
    }
    if (PERCUSSIVE.range.contains(instrumentNumber) ||
      CHROMATIC_PERCUSSION.range.contains(instrumentNumber)) {
      pattern.setVoice(9)
    }
    pattern
  }

  def convertNote(note: Note, instrumentNumber: Int): theory.Note = {
    new theory.Note(s"${note.name.toString}${note.intonation.toString}${note.octave}")
      .setDuration(note.duration)
      .setPercussionNote(PERCUSSIVE.range.contains(instrumentNumber) ||
                         CHROMATIC_PERCUSSION.range.contains(instrumentNumber))
  }
}
