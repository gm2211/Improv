package midi

import instruments.InstrumentType
import instruments.InstrumentType.InstrumentType
import jm.music.data.Score
import jm.music.{data => jmData}
import jm.util.Read
import org.slf4j.LoggerFactory
import representation.KeySignature.{KeyQuality, Major, Minor}
import representation._
import training.segmentation.PhraseSegmenter
import utils.ImplicitConversions.{toEnhancedIterable, toFasterMutableList}
import utils.NumericUtils
import utils.collections.CollectionUtils
import utils.functional.FunctionalUtils

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.concurrent.duration.{NANOSECONDS, TimeUnit}
import scala.util.Try
import scalaz.Scalaz._

object JMusicMIDIParser extends MIDIParserFactory with Function[String, JMusicMIDIParser] {
  override def apply(filename: String) = {
    val score: Score = getScore(filename)
    new JMusicMIDIParser(score, PhraseSegmenter.getDefault())
  }

  def apply(phraseSegmenter: PhraseSegmenter)(filename: String) = {
    val score: Score = getScore(filename)
    new JMusicMIDIParser(score, phraseSegmenter)
  }

  private def getScore(filename: String): Score = {
    val score: Score = new Score()
    Read.midi(score, filename)
    score
  }
}

class JMusicMIDIParser(
  val score: jmData.Score,
  val phraseSegmenter: PhraseSegmenter) extends MIDIParser {

  def split(phrase: Phrase): Traversable[Phrase] = {
    phraseSegmenter.segment(phrase)
  }

  override def getAllMultiVoicePhrases: List[List[Phrase]] = {
    (0 until score.getPartList.size()).map(getMultiVoicePhrases).toList
  }

  def getMultiVoicePhrase(partNum: Int): Option[Phrase] =
    JMusicParserUtils.convertPart(score.getPart(partNum))

  private val getPhrasesM = FunctionalUtils.memoized[Int, Traversable[Phrase]]((partNum: Int) => {
    getMultiVoicePhrase(partNum).map(phrase => split(Phrase.mergePhrases(phrase))).getOrElse(List())
  })

  override def getPhrases(partNum: Int): Traversable[Phrase] = getPhrasesM(partNum)

  private val getMultiVoicePhrasesM = FunctionalUtils.memoized((partNum: Int) => {
    getMultiVoicePhrase(partNum).map(split).getOrElse(List())
  })

  override def getMultiVoicePhrases(partNum: Int): List[Phrase] = getMultiVoicePhrasesM(partNum).toList

  private val partIndexByInstrumentM = FunctionalUtils.memoized[mutable.MultiMap[InstrumentType, Int]]({
    val partsArray = score.getPartArray
    (0 until partsArray.size)
      .groupByMultiMap(index => InstrumentType.classify(partsArray(index).getInstrument))
  })

  override def getPartIndexByInstrument: mutable.MultiMap[InstrumentType, Int] = partIndexByInstrumentM

  def getInstrumentsCounts: Map[InstrumentType.InstrumentCategory, Int] = {
    val parts = score.getPartArray
    val instruments = parts.map(i => InstrumentType.classify(i.getInstrument))
    instruments.groupBy(identity).mapValues(_.length)
  }
}

object JMusicParserUtils {
  private val log = LoggerFactory.getLogger(getClass)

  def getTempo(part: jmData.Part): Double = {
    Try(part.getTempo)
      .filter(_ >= 0)
      .orElse(Try(part.getMyScore.getTempo))
      .getOrElse(MusicalElement.DEFAULT_TEMPO_BPM)
  }

  def getTempo(phrase: jmData.Phrase): Double = {
    Try(phrase.getTempo)
      .filter(_ >= 0)
      .getOrElse(getTempo(phrase.getMyPart))
  }


  def convertNote(jmNote: jmData.Note): Option[MusicalElement] = {
    val tempoBPM = getTempo(jmNote.getMyPhrase)
    val fromBPM = (value: Double) => MusicalElement.fromBPM(value, tempoBPM)
    val duration = fromBPM(jmNote.getRhythmValue)
    val startTime = fromBPM(jmNote.getNoteStartTime.get)

    if (jmNote.isRest) {
      Some(
        Rest()
          .withDuration(duration)
          .withStartTime(startTime)
      )
    } else {
      val notePitch = jmNote.getPitchType match {
        case jmData.Note.MIDI_PITCH =>
          jmNote.getPitch
        case _ =>
          jmData.Note.freqToMidiPitch(jmNote.getFrequency)
      }
      val intonation = if (jmNote.isFlat) Flat else if (jmNote.isSharp) Sharp else Natural
      val (noteName, _, _) = Note.parseString(jmNote.getNote)
      val loudness = Loudness(jmNote.getDynamic)

      noteName.flatMap(name => Some(Note(name = name,
        octave = Note.pitchToOctave(notePitch),
        midiPitch = notePitch,
        durationNS = duration,
        accidental = intonation,
        loudness = loudness,
        startTimeNS = startTime)))
    }
  }

  def extractSignature(part: jmData.Part): KeySignature = {
    val midiSignature = part.getMyScore.getKeySignature
    var numOfFlats = 0
    var numOfSharps = 0

    if (midiSignature > 0) {
      numOfSharps = midiSignature
    } else {
      numOfFlats = midiSignature * -1
    }

    val keyQuality = if (part.getKeyQuality == 0) Major else Minor

    KeySignature(flats = numOfFlats, sharps = numOfSharps, keyQuality)
  }

  val convertPart = FunctionalUtils.memoized((part: jmData.Part) => {
    val phrases = part.getPhraseList.map(convertPhrase)
    val keySignature = extractSignature(part)
    Phrase.createPolyphonicPhrase(phrases.toList, getTempo(part), keySignature)
  })

  val convertPhrase = FunctionalUtils.memoized((phrase: jmData.Phrase) => {
    val elements = mutable.MutableList[MusicalElement]()
    val jmNotes = phrase.getNoteList.toList.sortBy(_.getNoteStartTime.get)
    val errorMargin = BigInt(1000)
    var prevEnd = BigInt(0)

    for (jmNote <- jmNotes) {
      val convertedNote = convertNote(jmNote)
      val gap = convertedNote.map(_.getStartTimeNS - prevEnd).getOrElse(BigInt(0))
      if ( gap > errorMargin) {
        Phrase.addToPhraseElements(Rest(durationNS = gap), elements)
      }
      convertedNote.foreach(Phrase.addToPhraseElements(_, elements))
      convertedNote.foreach(n => prevEnd = NumericUtils.max(n.getEndTimeNS, prevEnd))
    }

    new Phrase(elements.toList, tempoBPM = getTempo(phrase))
  })
}

