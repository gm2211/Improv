package genetic

trait SolutionSelector[Elem] {
  def selectSolution(
    previousSolution: Option[Elem] = None,
    candidateSolutions: List[Elem],
    constraints: List[(Elem) => Boolean]): Option[Elem]
}
