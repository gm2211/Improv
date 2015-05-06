package midi

import java.io.File
import javax.sound.midi.MidiSystem

import instruments.InstrumentType.{InstrumentCategory, InstrumentType}
import org.jfugue.midi.MidiFileManager
import org.jfugue.parser.ParserListenerAdapter
import org.jfugue.theory.{Chord, Note}
import representation.Phrase

object JFugueMIDIParser {
  def apply(filename: String): JFugueMIDIParser = {
    MidiFileManager.loadPatternFromMidi(new File(filename)).getPattern
    val parser = new org.jfugue.midi.MidiParser()
    val parserListener = new JFugueParseListener
    parser.addParserListener(parserListener)
    parser.parse(MidiSystem.getSequence(new File(filename)))
    parserListener.buildStructure
  }
}

class JFugueMIDIParser extends MIDIParser {
  override def getInstrumentsCounts: Map[InstrumentCategory, Int] = Map()

  override def getPartIndexByInstrument: Map[InstrumentType, Array[Int]] = Map()

  override def getPhrases(partNum: Int): Iterator[Phrase] = List().iterator

  override def getPhrase(phraseNum: Int, partNum: Int): Phrase = Phrase.builder.build
}

class JFugueParseListener extends ParserListenerAdapter {
  def buildStructure: JFugueMIDIParser = new JFugueMIDIParser //TODO pass parsed content into class

  override def onChordParsed(chord: Chord): Unit = super.onChordParsed(chord)

  override def onInstrumentParsed(instrument: Byte): Unit = super.onInstrumentParsed(instrument)

  override def onNoteParsed(note: Note): Unit = super.onNoteParsed(note)

  override def onBarLineParsed(id: Long): Unit = super.onBarLineParsed(id)

}
