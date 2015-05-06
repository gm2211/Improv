package actors.composers

import midi.MIDIParser
import representation.Phrase
import utils.builders.Count

import scala.util.Try

case class MIDIReaderComposerBuilder[
FileNameCount <: Count,
PartNumCount <: Count](
                        filename: String,
                        partNum: Int,
                        midiParser: Option[MIDIParser]) {

}

object MIDIReaderComposer {

}

class MIDIReaderComposer(val filename: String, val partNum: Int) extends Composer {
  val midiReader: MIDIParser
  var phraseIterOpt: Option[Iterator[Option[Phrase]]] = Try(midiReader.getPhrases(partNum)).toOption

  override def compose(previousPhrase: Phrase): Phrase = {
    phraseIterOpt.flatMap(_.next())
      .getOrElse(Phrase.builder.build())
  }
}
