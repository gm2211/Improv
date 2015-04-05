package instruments

import overtone.wrapper.OvertoneWrapper

abstract class OvertoneInstrument(val overtoneWrapper: OvertoneWrapper = new OvertoneWrapper()) extends Instrument {
}
