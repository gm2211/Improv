package actors.composers

import cbr.CaseIndex
import genetic.{RandomSelector, PopulationSelector}
import instruments.InstrumentType.InstrumentType
import representation.Phrase

object CBRComposer {
  private val DEFAULT_NEIGHBOURS_COUNT: Int = 10
}

class CBRComposer(
    private val caseIndex: CaseIndex[(InstrumentType, Phrase)],
    populationSelector: Option[PopulationSelector[(InstrumentType, Phrase)]] = None) extends Composer {

  private val populationSelector_ = populationSelector.getOrElse(new RandomSelector)

  override def compose(phrasesByOthers: Traversable[(InstrumentType, Phrase)]): Option[Phrase] = {
    val solutionPopulation = phrasesByOthers
      .flatMap(caseIndex.findSolutionsToSimilarProblems(_, CBRComposer.DEFAULT_NEIGHBOURS_COUNT))
    populationSelector_.selectSolution(solutionPopulation, List()).map(_._2)
  }
}
