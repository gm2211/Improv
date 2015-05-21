package demos

import cbr.description.DescriptionCreator
import representation.Phrase
import storage.KDTreeIndex
import training.TrainingUtils

object DemoPopulateDB {
  def run(filename: String): Unit = {
    val index = KDTreeIndex.loadOrCreateDefault[Phrase](DescriptionCreator.getDefaultPhraseDescriptionCreator)
//    TrainingUtils.addCasesToIndex(index, filename)
    println(TrainingUtils.getCasesFromMIDI(filename))
    index.save()
  }
}
