package genetic

trait ChromosomeGenerator[Elem, Chromosome] {
  def createChromosome(elem: Elem): Chromosome
  def fromChromosome(chromosome: Chromosome): Elem
}
