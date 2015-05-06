package actors.composers

import midi.{JMusicMIDIParser, MIDIParser}
import representation.Phrase
import utils.builders.{Zero, IsOnce, Once, Count}

import scala.util.Try

case class MIDIReaderComposerBuilder[
  FileNameCount <: Count,
  PartNumCount <: Count](
    filename: Option[String] = None,
    partNum: Option[Int] = None,
    midiParser: Option[MIDIParser] = None){

  def withFilename(filename: String) = copy[Once, PartNumCount](filename = Some(filename))

  def withPartNum(partNum: Int) = copy[FileNameCount, Once](partNum = Some(partNum))

  def withMIDIParser(midiParser: MIDIParser) = copy[FileNameCount, PartNumCount](midiParser = Some(midiParser))

  def build[A <: FileNameCount : IsOnce, B <: PartNumCount : IsOnce] =
    new MIDIReaderComposer(this.asInstanceOf[MIDIReaderComposerBuilder[Once, Once]])
}

object MIDIReaderComposer {
  def builder = new MIDIReaderComposerBuilder[Zero, Zero]
}

class MIDIReaderComposer(builder: MIDIReaderComposerBuilder[Once, Once]) extends Composer {
  val filename: String = builder.filename.get
  val partNum: Int = builder.partNum.get
  val midiReader: MIDIParser = builder.midiParser.getOrElse(JMusicMIDIParser(filename))
  var phraseIterOpt: Option[Iterator[Option[Phrase]]] = Try(midiReader.getPhrases(partNum)).toOption

  override def compose(previousPhrase: Phrase): Phrase = {
    phraseIterOpt.flatMap(_.next())
      .getOrElse(Phrase.builder.build())
  }
}
