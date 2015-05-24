package actors.composers

import cbr.CaseIndex
import representation.Phrase

class CBRComposer(private val caseIndex: CaseIndex[Phrase]) extends Composer {
  override def compose(phrasesByOthers: Traversable[Phrase]): Option[Phrase] = {
//    val solutionPopulation = phrasesByOthers.flatMap(caseIndex.findKNearestNeighbours())
    None
  }
}
