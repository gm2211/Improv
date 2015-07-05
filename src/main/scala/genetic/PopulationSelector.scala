package genetic

trait PopulationSelector[Elem] {
  def selectSolution(candidates: List[Elem], constraints: List[(Elem) => Boolean]): Option[Elem]
}
