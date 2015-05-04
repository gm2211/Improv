package tests.instruments

import tests.utils.CollectionUtils

import scala.reflect.ClassTag

object InstrumentType {
  type InstrumentType = InstrumentCategory

  sealed abstract class InstrumentTypeObject[T <: InstrumentCategory](implicit m: ClassTag[T]) {
    val range: Range
    def apply(): InstrumentType = {
      val instrumentNumber = new Integer(CollectionUtils.chooseRandom(range).getOrElse(0))
      m.runtimeClass.getConstructors()(0).newInstance(instrumentNumber).asInstanceOf[InstrumentType]
    }
  }

  object PIANO extends InstrumentTypeObject[PIANO] { val range = 1 to 8 }
  object CHROMATIC_PERCUSSION extends InstrumentTypeObject[CHROMATIC_PERCUSSION] { val range = 9 to 16 }
  object ORGAN extends InstrumentTypeObject[ORGAN] { val range = 17 to 24 }
  object GUITAR extends InstrumentTypeObject[GUITAR] { val range = 25 to 32 }
  object BASS extends InstrumentTypeObject[BASS] { val range = 33 to 40 }
  object STRINGS extends InstrumentTypeObject[STRINGS] { val range = 41 to 48 }
  object ENSEMBLE extends InstrumentTypeObject[ENSEMBLE] { val range = 49 to 56 }
  object BRASS extends InstrumentTypeObject[BRASS] { val range = 57 to 64 }
  object REED extends InstrumentTypeObject[REED] { val range = 65 to 72 }
  object PIPE extends InstrumentTypeObject[PIPE] { val range = 73 to 80 }
  object SYNTH_LEAD extends InstrumentTypeObject[SYNTH_LEAD] { val range = 81 to 88 }
  object SYNTH_PAD extends InstrumentTypeObject[SYNTH_PAD] { val range = 89 to 96 }
  object SYNTH_EFFECTS extends InstrumentTypeObject[SYNTH_EFFECTS] { val range = 97 to 104 }
  object ETHNIC extends InstrumentTypeObject[ETHNIC] { val range = 105 to 112 }
  object PERCUSSIVE extends InstrumentTypeObject[PERCUSSIVE] { val range = 113 to 120 }
  object SOUND_EFFECTS extends InstrumentTypeObject[SOUND_EFFECTS] { val range = 121 to 128 }
  object UNKNOWN extends InstrumentTypeObject[UNKNOWN] { val range = 129 to Int.MaxValue }

  sealed abstract class InstrumentCategory(val instrumentNumber: Int, val instrTypeObj: InstrumentTypeObject[_]) {
    require(instrTypeObj.range contains instrumentNumber)
  }

  case class PIANO(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber, PIANO)
  case class CHROMATIC_PERCUSSION (override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber, CHROMATIC_PERCUSSION)
  case class ORGAN(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber, ORGAN)
  case class GUITAR(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber, GUITAR)
  case class BASS(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber, BASS)
  case class STRINGS(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber, STRINGS)
  case class ENSEMBLE(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber, ENSEMBLE)
  case class BRASS(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber, BRASS)
  case class REED(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber, REED)
  case class PIPE(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber, PIPE)
  case class SYNTH_LEAD(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber, SYNTH_LEAD)
  case class SYNTH_PAD(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber, SYNTH_PAD)
  case class SYNTH_EFFECTS(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber, SYNTH_EFFECTS)
  case class ETHNIC(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber, ETHNIC)
  case class PERCUSSIVE(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber, PERCUSSIVE)
  case class SOUND_EFFECTS(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber, SOUND_EFFECTS)
  case class UNKNOWN(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber, UNKNOWN)

  def classify(instrNumber: Int): InstrumentCategory = instrNumber + 1 match {
    case it if PIANO.range contains it => PIANO(instrNumber)
    case it if CHROMATIC_PERCUSSION.range contains it => CHROMATIC_PERCUSSION(instrNumber)
    case it if ORGAN.range contains it => ORGAN(instrNumber)
    case it if GUITAR.range contains it => GUITAR(instrNumber)
    case it if BASS.range contains it => BASS(instrNumber)
    case it if STRINGS.range contains it => STRINGS(instrNumber)
    case it if ENSEMBLE.range contains it => ENSEMBLE(instrNumber)
    case it if BRASS.range contains it => BRASS(instrNumber)
    case it if REED.range contains it => REED(instrNumber)
    case it if PIPE.range contains it => PIPE(instrNumber)
    case it if SYNTH_LEAD.range contains it => SYNTH_LEAD(instrNumber)
    case it if SYNTH_PAD.range contains it => SYNTH_PAD(instrNumber)
    case it if SYNTH_EFFECTS.range contains it => SYNTH_EFFECTS(instrNumber)
    case it if ETHNIC.range contains it => ETHNIC(instrNumber)
    case it if PERCUSSIVE.range contains it => PERCUSSIVE(instrNumber)
    case it if SOUND_EFFECTS.range contains it => SOUND_EFFECTS(instrNumber)
    case _ => UNKNOWN(instrNumber)
  }
}

