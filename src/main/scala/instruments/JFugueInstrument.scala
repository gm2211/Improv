package instruments

import java.util.concurrent.Executors
import javax.sound.midi.Sequence

import designPatterns.observer.{EventNotification, Observable}
import instruments.InstrumentType.{CHROMATIC_PERCUSSION, InstrumentType, PERCUSSIVE, PIANO}
import org.jfugue.async.Listener
import org.jfugue.midi.MidiParserListener
import org.jfugue.pattern.Pattern
import org.jfugue.player.Player
import org.jfugue.player.PlayerEvents.FINISHED_PLAYING
import org.jfugue.{async, theory}
import org.slf4j.LoggerFactory
import org.staccato.StaccatoParser
import representation._
import utils.ImplicitConversions.{anyToRunnable, toEnhancedTraversable}

import scala.math
import scalaz.Scalaz._

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
  val MAX_VOICE: Int = 15
  val DEFAULT_TEMPO: Int = 120

  def createPattern(musicalElement: MusicalElement, instrumentNumber: Int): Pattern = {
    createPatternHelper(musicalElement, instrumentNumber)
      .setInstrument(instrumentNumber)
      .setTempo(DEFAULT_TEMPO)
  }

  def createPatternHelper(musicalElement: MusicalElement, instrumentNumber: Int): Pattern = {
    val pattern: Pattern = musicalElement match {
      case note: Note =>
        convertNote(note, instrumentNumber).getPattern
      case rest: Rest =>
        theory.Note.createRest(rest.duration).getPattern
      case chord: Chord =>
        createChordPattern(instrumentNumber, chord)
      case phrase: Phrase =>
        createPhrasePattern(phrase, instrumentNumber)
    }
    if (PERCUSSIVE.range.contains(instrumentNumber) ||
      CHROMATIC_PERCUSSION.range.contains(instrumentNumber)) {
      pattern.setVoice(9)
    }
    pattern
  }

  def convertPolyphonicPhrase(phrase: Phrase, instrumentNumber: Int): String = {
    phrase.polyphony.option {
      phrase.musicalElements.asInstanceOf[List[Phrase]].zipped.map { case musicalElements =>
        musicalElements.map {
          case Some(elem) =>
            s"@${elem.getStartTime} ${createPatternHelper(elem, instrumentNumber)}"
          case _ =>
            ""
        }.mkString(" ")
      }.mkString(" ")
    }.getOrElse("")
  }

  def createPhrasePattern(phrase: Phrase, instrumentNumber: Int): Pattern = phrase match {
    case polyphonicPhrase@Phrase(_, true, _, _) =>
      new Pattern(convertPolyphonicPhrase(polyphonicPhrase, instrumentNumber))
    case normalPhrase@Phrase(_, false, _, _) =>
      new Pattern(normalPhrase.map(createPatternHelper(_, instrumentNumber).toString).mkString(" "))
  }

  def createChordPattern(instrumentNumber: Int, chord: Chord): Pattern =
    new Pattern(chord.notes.map(convertNote(_, instrumentNumber).getPattern.toString).mkString("+"))

  def convertNote(note: Note, instrumentNumber: Int): theory.Note = {
    new theory.Note(s"${note.name.toString}${note.intonation.toString}${note.octave}")
      .setDuration(note.duration)
      .setOnVelocity(note.loudness.loudness.toByte)
      .setOffVelocity(0)
      .setPercussionNote(PERCUSSIVE.range.contains(instrumentNumber) ||
                         CHROMATIC_PERCUSSION.range.contains(instrumentNumber))
  }

  def mergePatterns(patterns: Traversable[Pattern]): String = {
    var phrasePatternString = ""
    for ((curPattern, voice) <- patterns.toStream.zipWithIndex) {
      val nonPercussionVoice = if (voice < 9) voice + 1 else voice + 1
      if (nonPercussionVoice <= MAX_VOICE) {
        phrasePatternString += s" ${curPattern.setVoice(nonPercussionVoice).toString}"
      }
    }
    phrasePatternString
  }

  def toSequence(pattern: Pattern): Sequence = {
    val parser = new StaccatoParser()
    val midiListener = new MidiParserListener()
    parser.addParserListener(midiListener)
    parser.parse(pattern)
    midiListener.getSequence
  }
}
