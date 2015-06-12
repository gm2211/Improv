package representation

import representation.KeySignature.{KeyQuality, Scale}

object KeySignature {
  val MAX_FLATS = 7
  val MIN_FLATS = 0
  val MAX_SHARPS = 7
  val MIN_SHARPS = 0

  sealed trait KeyQuality
  case object Minor extends KeyQuality
  case object Major extends KeyQuality

  val scales = List(
    CMaj, GMaj, DMaj, AMaj, EMaj, BMaj, FSharpMaj,
    CSharpMaj, FMaj, BbMaj, EbMaj, AbMaj, DbMaj, GbMaj,
    CbMaj, AMin, EMin, BMin, FSharpMin, CSharpMin, GSharpMin,
    DSharpMin, ASharpMin, DMin, GMin, CMin, FMin,
    BbMin, EbMin, AbMin)

  val scalesBySignature: Map[KeySignature, Scale] =
    scales.map(scale => scale.keySignature -> scale).toMap

  sealed trait Scale {
    val keySignature: KeySignature 
    def getPhrase: Phrase = {
      Phrase() //TODO: Provide a real implementation
    }

    /**
     * If this scale is a major scale, it returns the relative minor and viceversa
     */
    def getRelativeScale: Scale = {
      val quality = keySignature.quality match {
        case Minor =>
          Major
        case Major =>
          Minor
      }
      scalesBySignature.get(keySignature.withQuality(quality)).get
    }
  }

  case object CMaj extends Scale {
    override val keySignature = KeySignature(0, 0, Major)
  }

  case object GMaj extends Scale {
    override val keySignature = KeySignature(0, 1, Major)
  }

  case object DMaj extends Scale {
    override val keySignature = KeySignature(0, 2, Major)
  }

  case object AMaj extends Scale {
    override val keySignature = KeySignature(0, 3, Major)
  }

  case object EMaj extends Scale {
    override val keySignature = KeySignature(0, 4, Major)
  }

  case object BMaj extends Scale {
    override val keySignature = KeySignature(0, 5, Major)
  }

  case object FSharpMaj extends Scale {
    override val keySignature = KeySignature(0, 6, Major)
  }

  case object CSharpMaj extends Scale {
    override val keySignature = KeySignature(0, 7, Major)
  }

  case object FMaj extends Scale {
    override val keySignature = KeySignature(1, 0, Major)
  }

  case object BbMaj extends Scale {
    override val keySignature = KeySignature(2, 0, Major)
  }

  case object EbMaj extends Scale {
    override val keySignature = KeySignature(3, 0, Major)
  }

  case object AbMaj extends Scale {
    override val keySignature = KeySignature(4, 0, Major)
  }

  case object DbMaj extends Scale {
    override val keySignature = KeySignature(5, 0, Major)
  }

  case object GbMaj extends Scale {
    override val keySignature = KeySignature(6, 0, Major)
  }

  case object CbMaj extends Scale {
    override val keySignature = KeySignature(7, 0, Major)
  }

  // Minors
  
  case object AMin extends Scale {
    override val keySignature = KeySignature(0, 0, Minor)
  }

  case object EMin extends Scale {
    override val keySignature = KeySignature(0, 1, Minor)
  }

  case object BMin extends Scale {
    override val keySignature = KeySignature(0, 2, Minor)
  }

  case object FSharpMin extends Scale {
    override val keySignature = KeySignature(0, 3, Minor)
  }

  case object CSharpMin extends Scale {
    override val keySignature = KeySignature(0, 4, Minor)
  }

  case object GSharpMin extends Scale {
    override val keySignature = KeySignature(0, 5, Minor)
  }

  case object DSharpMin extends Scale {
    override val keySignature = KeySignature(0, 6, Minor)
  }

  case object ASharpMin extends Scale {
    override val keySignature = KeySignature(0, 7, Minor)
  }

  case object DMin extends Scale {
    override val keySignature = KeySignature(1, 0, Minor)
  }

  case object GMin extends Scale {
    override val keySignature = KeySignature(2, 0, Minor)
  }

  case object CMin extends Scale {
    override val keySignature = KeySignature(3, 0, Minor)
  }

  case object FMin extends Scale {
    override val keySignature = KeySignature(4, 0, Minor)
  }

  case object BbMin extends Scale {
    override val keySignature = KeySignature(5, 0, Minor)
  }

  case object EbMin extends Scale {
    override val keySignature = KeySignature(6, 0, Minor)
  }

  case object AbMin extends Scale {
    override val keySignature = KeySignature(7, 0, Minor)
  }

}

case class KeySignature(flats: Int, sharps: Int, quality: KeyQuality) {
  def withQuality(quality: KeyQuality): KeySignature = copy(quality = quality)

  require(Range(KeySignature.MAX_FLATS, KeySignature.MIN_FLATS).contains(flats)
    , s"You cannot have more than ${KeySignature.MAX_FLATS} flats or less than ${KeySignature.MIN_FLATS}")
  require(Range(KeySignature.MAX_SHARPS, KeySignature.MIN_SHARPS).contains(sharps)
    , s"You cannot have more than ${KeySignature.MAX_SHARPS} sharps or less than ${KeySignature.MIN_SHARPS}")

  def toScale: Option[Scale] = KeySignature.scalesBySignature.get(this)
}