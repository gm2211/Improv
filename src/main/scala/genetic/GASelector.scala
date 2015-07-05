package genetic

import scala.collection.mutable.ListBuffer
import scala.util.{Random, Try}

class GASelector[Elem, Chromosome](
    fitnessFunction: Elem => Double,
    stoppingCriteria: StoppingCriteria = IterationLimitReached(20),
    chromosomeGenerator: ChromosomeGenerator[Elem, Chromosome],
    geneticOperator: GeneticOperator[Chromosome]) extends PopulationSelector[Elem] {
  val survivalRate = 2.0/3.0


  override def selectSolution(
      candidates: List[Elem],
      constraints: List[(Elem) => Boolean]): Option[Elem] = {

    var population = candidates
    var iterationCount = 0

    while (! shouldTerminate(iterationCount)) {
      val ratedPopulation = candidates.map(candidate => (candidate, fitnessFunction(candidate)))
      val survivors = stochasticSampling(ratedPopulation, (ratedPopulation.size * survivalRate).toInt)
      population = survivors ++ breed(survivors)
      iterationCount += 1
    }

    Try(population.maxBy(fitnessFunction)).toOption
  }

  //TODO: consider moving this to CollectionUtils
  private def stochasticSampling(ratedPopulation: List[(Elem, Double)], sampleSize: Int): List[Elem] = {
    val maxFitness = ratedPopulation.maxBy(_._2)._2
    val sample = ListBuffer[Elem]()

    while (sample.size < sample.size) {
      var selectedElement: Option[Elem] = None
      while (selectedElement.isEmpty) {
        val candidate = ratedPopulation(Random.nextInt(ratedPopulation.size))
        if (Random.nextDouble() < candidate._2 / maxFitness){
          selectedElement = Some(candidate._1)
        }
      }
      sample += selectedElement.get
    }
    sample.toList
  }

  private def shouldTerminate(iterationCount: Int): Boolean = stoppingCriteria match {
    case limitRule: IterationLimitReached =>
      iterationCount >= limitRule.limit
    case _ =>
      false
  }


  def breed(survivors: List[Elem]): List[Elem] = {
    survivors.combinations(2).collect { case parent1 :: parent2 :: whatever =>
        val chromosome1 = chromosomeGenerator.createChromosome(parent1)
        val chromosome2 = chromosomeGenerator.createChromosome(parent2)

        chromosomeGenerator.fromChromosome(geneticOperator.crossOver(chromosome1, chromosome2));
    }.toList
  }
}
