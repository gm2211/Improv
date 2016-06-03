package genetic

trait GeneticOperator[Chromosome] {
  def crossOver(e1: Chromosome, e2: Chromosome): Chromosome
}
