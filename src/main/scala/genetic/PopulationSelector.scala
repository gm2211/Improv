package genetic

trait PopulationSelector[Elem] {
  def selectSolution(candidates: Traversable[Elem], constraints: Traversable[(Elem) => Boolean]): Option[Elem]
}
