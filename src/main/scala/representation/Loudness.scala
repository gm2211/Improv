package representation

object Loudness {
  def apply(value: Int): Loudness = value match {
    case it if SILENT.loudness until PPP.loudness contains it =>
      SILENT
    case it if PPP.loudness until PP.loudness contains it =>
      PPP
    case it if PP.loudness until P.loudness contains it =>
      PP
    case it if P.loudness until MP.loudness contains it =>
      P
    case it if MP.loudness until MF.loudness contains it =>
      MP
    case it if MF.loudness until F.loudness contains it =>
      MF
    case it if F.loudness until FF.loudness contains it =>
      F
    case it if FF.loudness until FFF.loudness contains it =>
      FF
    case it if FFF.loudness until Int.MaxValue contains it =>
      FFF
  }
}

sealed trait Loudness {
  val loudness: Int
}

case object SILENT extends Loudness {
  override val loudness: Int = 0
}

case object PPP extends Loudness {
  override val loudness: Int = 10
}

case object PP extends Loudness {
  override val loudness: Int = 25
}

case object P extends Loudness {
  override val loudness: Int = 50
}

case object MP extends Loudness {
  override val loudness: Int = 60
}

case object MF extends Loudness {
  override val loudness: Int = 70
}

case object F extends Loudness {
  override val loudness: Int = 85
}

case object FF extends Loudness {
  override val loudness: Int = 100
}

case object FFF extends Loudness {
  override val loudness: Int = 120
}
