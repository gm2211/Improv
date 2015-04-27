package representation

object Intonation {
  def apply(str: String) = Option(str).getOrElse("").toLowerCase match {
    case "b" =>
      Flat
    case "#" =>
      Sharp
    case _ =>
      Natural
  }
}

sealed trait Intonation
case object Flat extends Intonation
case object Sharp extends Intonation
case object Natural extends Intonation

