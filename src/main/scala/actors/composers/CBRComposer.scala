package actors.composers

import cbr.{CaseIndex, MusicalCase}
import genetic.{PopulationSelector, RandomSelector}
import representation.Phrase
import utils.collections.CollectionUtils

object CBRComposer {
  private val DEFAULT_NEIGHBOURS_COUNT: Int = 10
}

class CBRComposer(
    private val caseIndex: CaseIndex[MusicalCase],
    populationSelector: Option[PopulationSelector[MusicalCase]] = None) extends Composer {

  private val populationSelector_ = populationSelector.getOrElse(new RandomSelector[MusicalCase])


  override def compose(phrasesByOthers: Traversable[MusicalCase]): Option[Phrase] = {
    var solutionPopulation: List[MusicalCase] = phrasesByOthers
      .flatMap(caseIndex.findSolutionsToSimilarProblems(_, CBRComposer.DEFAULT_NEIGHBOURS_COUNT)).toList
    if (solutionPopulation.isEmpty) {
      solutionPopulation = chooseRandom(caseIndex)
    }
    val constraints: List[(MusicalCase) => Boolean] = List()
    populationSelector_.selectSolution(solutionPopulation, constraints).map(_.phrase)
  }

  def chooseRandom(caseIndex: CaseIndex[MusicalCase]): List[MusicalCase] = {
    CollectionUtils.chooseRandom(caseIndex.toIterable).map{ caseDesc =>
      caseIndex.findSolutionsToSimilarProblems(caseDesc, CBRComposer.DEFAULT_NEIGHBOURS_COUNT)
    }.getOrElse(List())
  }
}
