package representation

object Accidental {
  def apply(str: String) = Option(str).getOrElse("").toLowerCase match {
    case "b" =>
      Flat
    case "#" =>
      Sharp
    case _ =>
      Natural
  }
}

sealed trait Accidental

case object Flat extends Accidental { override def toString: String = "b"}

case object Sharp extends Accidental { override def toString: String = "#"}

case object Natural extends Accidental { override def toString: String = ""}

