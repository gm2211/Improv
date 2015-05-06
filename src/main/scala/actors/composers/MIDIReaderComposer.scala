package actors.composers

import midi.{MIDIParserFactory, JMusicMIDIParser, MIDIParser}
import representation.Phrase
import utils.builders.{Count, IsOnce, Once, Zero}

case class MIDIReaderComposerBuilder[
  FileNameCount <: Count,
  PartNumCount <: Count](
    filename: Option[String] = None,
    partNum: Option[Int] = None,
    midiParserFactory: Option[MIDIParserFactory] = None){

  def withFilename(filename: String) = copy[Once, PartNumCount](filename = Some(filename))

  def withPartNum(partNum: Int) = copy[FileNameCount, Once](partNum = Some(partNum))

  def withMIDIParser(midiParserFactory: MIDIParserFactory) = copy[FileNameCount, PartNumCount](midiParserFactory = Some(midiParserFactory))

  def build[A <: FileNameCount : IsOnce, B <: PartNumCount : IsOnce] =
    new MIDIReaderComposer(this.asInstanceOf[MIDIReaderComposerBuilder[Once, Once]])
}

object MIDIReaderComposer {
  def builder = new MIDIReaderComposerBuilder[Zero, Zero]
}

class MIDIReaderComposer(builder: MIDIReaderComposerBuilder[Once, Once]) extends Composer {
  val filename: String = builder.filename.get
  val partNum: Int = builder.partNum.get
  val midiReader: MIDIParser = builder.midiParserFactory.getOrElse(JMusicMIDIParser)(filename)
  val phraseIterator: Iterator[Phrase] = midiReader.getPhrases(partNum)

  override def compose(previousPhrase: Phrase): Option[Phrase] = {
    if (phraseIterator.hasNext)
      Some(phraseIterator.next())
    else
      None
  }
}
