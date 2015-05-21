package actors.composers

import cbr.CaseIndex
import cbr.description.CaseDescription
import representation.Phrase

class CBRComposer(private val caseIndex: CaseIndex[CaseDescription, Phrase]) extends Composer {
  override def compose(phrasesByOthers: List[Phrase]): Option[Phrase] = {
    val solutionPopulation = phrasesByOthers.flatMap(caseIndex.findKNearestNeighbours())
  }
}
