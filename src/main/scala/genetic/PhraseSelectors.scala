package genetic

import cbr.MusicalCase
import representation.Phrase

object PhraseSelectors {
  def getRandomSelector = new RandomSelector[MusicalCase]
  def getGASelector = {
    new GASelector[MusicalCase, MusicalCase](
      ANNFitnessFunction.getDefault,
      chromosomeGenerator = new ChromosomeGenerator[MusicalCase, MusicalCase] {
        override def createChromosome(elem: MusicalCase): MusicalCase = {
          elem
        }

        override def fromChromosome(chromosome: MusicalCase): MusicalCase = {
          chromosome
        }
      },
      geneticOperator = new MusicalCaseCrossoverOperator
    )
  }
}
