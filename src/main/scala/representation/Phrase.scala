package representation

import scala.collection.mutable.ListBuffer

case class PhraseBuilder(private val musicalElementsBuf: ListBuffer[MusicalElement] = ListBuffer()) {
  def musicalElements: List[MusicalElement] = musicalElementsBuf.toList

  def withMusicalElements(musicalElements: Iterable[MusicalElement]) =
    copy(musicalElementsBuf = musicalElements.to[ListBuffer])

  def addMusicalElement(musicalElement: MusicalElement) = {
    musicalElementsBuf += musicalElement
    this
  }

  def build() = new Phrase(this)
}

object Phrase {
  def builder = new PhraseBuilder()
}

case class Phrase(builder: PhraseBuilder) extends MusicalElement with Iterable[MusicalElement] {
  val musicalElements: List[MusicalElement] = builder.musicalElements

  override def iterator: Iterator[MusicalElement] = musicalElements.iterator

  override def isEmpty: Boolean = musicalElements.isEmpty
}
