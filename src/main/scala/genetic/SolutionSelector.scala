package genetic

trait SolutionSelector[Elem] {
  def selectSolution(
    previousSolution: Elem,
    candidateSolutions: List[Elem],
    constraints: List[(Elem) => Boolean]): Option[Elem]
}
