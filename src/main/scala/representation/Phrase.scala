package representation

import scala.collection.mutable.ListBuffer

class PhraseBuilder() {
    private var _musicalElements = ListBuffer[MusicalElement]()
    def musicalElements: List[MusicalElement] = _musicalElements.toList

    def withMusicalElements(musElems: List[MusicalElement]) = {
        _musicalElements = musElems.to[ListBuffer]
        this
    }

    def addMusicalElement(musicalElement: MusicalElement) = {
        _musicalElements += musicalElement
        this
    }

    def build() = new Phrase(this)
}

object Phrase {
    def builder = new PhraseBuilder()
}

class Phrase(builder: PhraseBuilder) extends Iterable[MusicalElement] {
    val musicalElements: List[MusicalElement] = builder.musicalElements

    override def iterator: Iterator[MusicalElement] = musicalElements.iterator
}
