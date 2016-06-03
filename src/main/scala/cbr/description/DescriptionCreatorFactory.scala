package cbr.description

trait DescriptionCreatorFactory[Problem] {
  def make: DescriptionCreator[Problem]
}
