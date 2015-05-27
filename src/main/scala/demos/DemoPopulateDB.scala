package demos

import cbr.description.PhraseDescriptionCreators
import instruments.InstrumentType.InstrumentType
import representation.Phrase
import storage.KDTreeIndex
import training.TrainingUtils
import utils.IOUtils

object DemoPopulateDB {
  def run(resourceDirPath: String): Unit = {
    val index = KDTreeIndex.loadOrCreateDefault[(InstrumentType, Phrase)](PhraseDescriptionCreators.getDefault)
    val filenames = IOUtils.filesInDir(IOUtils.getResourcePath(resourceDirPath)).getOrElse(List())
    filenames.foreach(TrainingUtils.addCasesToIndex(index, _))
    index.save()
  }
}
