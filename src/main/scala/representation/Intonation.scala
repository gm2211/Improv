package representation

sealed trait Intonation
case object Flat extends Intonation
case object Sharp extends Intonation
case object Natural extends Intonation

