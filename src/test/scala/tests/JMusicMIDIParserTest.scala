package tests

import java.io.{FileOutputStream, File}
import javax.sound.midi.MidiSystem
import javax.sound.midi.spi.MidiFileWriter

import _root_.instruments.InstrumentType.PIANO
import _root_.instruments.JFugueUtils
import _root_.midi.{JMusicMIDIParser, JMusicParserUtils}
import _root_.utils.{IOUtils, ProfilingUtils}
import _root_.utils.ImplicitConversions.toEnhancedTraversable
import com.sun.media.sound.StandardMidiFileWriter
import org.jfugue.player.Player
import org.scalatest.FlatSpec
import representation.Phrase

import scala.util.Try

class JMusicMIDIParserTest extends FlatSpec {
  val resourcePath = IOUtils.getResourcePath("musicScores/shortPiano.mid")
  val parser = JMusicMIDIParser(resourcePath)

  "The midi parser" should "return a list of parts and their indexes grouped by instrument" in {
    val partsIndices = parser.getPartIndexByInstrument.get(PIANO(1)).get
    assert(partsIndices == Set(0, 1))
  }

  "The midi parser" should "correctly assemble a phrase" in {
    println()
//    val multiVoicePhrase = Phrase()
//      .withMusicalElements(Phrase().withMusicalElements(Rest(startTime = 0, duration = 0.5), Note(name = E, duration = 0.5, startTime = 1)),
//                           Phrase().withMusicalElements(Note(name = B, startTime = 0, duration = 0.5), Note(name = D, startTime = 1)),
//                           Phrase().withMusicalElements(Note(name = C, startTime = 0, duration = 0.5), Rest(startTime = 1)))
//      .withPolyphony(polyphony = true).get
    val multiVoicePhrase = JMusicParserUtils.convertPart(parser.score.getPart(0))
    val mergedPhrase = JMusicParserUtils.mergePhrases(multiVoicePhrase).getOrElse(Phrase())
    val splitTask = () => JMusicParserUtils.splitPhrase(mergedPhrase)

    val (timeElapsed, multiVoicePhraseRecovered) = ProfilingUtils.timeIt(splitTask(), 1)
    println(s"\nOn average it took $timeElapsed milliseconds to get $multiVoicePhraseRecovered")
    val theirSequence = JFugueUtils.toSequence(JFugueUtils.createPattern(multiVoicePhrase, PIANO(1)))
    val mySequence = JFugueUtils.toSequence(JFugueUtils.createPattern(multiVoicePhraseRecovered, PIANO(1)))
    val mergedSequence = JFugueUtils.toSequence(JFugueUtils.createPattern(mergedPhrase, PIANO(1)))
    val out1 = new FileOutputStream(IOUtils.getResourcePath("musicScores") + "/theirPiano.mid")
    val out2 = new FileOutputStream(IOUtils.getResourcePath("musicScores") + "/recovered.mid")
    val out3 = new FileOutputStream(IOUtils.getResourcePath("musicScores") + "/merged.mid")
    MidiSystem.write(theirSequence, 1, out1)
    MidiSystem.write(mySequence, 1, out2)
    MidiSystem.write(mergedSequence, 1, out3)
  }
}
