package demos

import cbr.MusicalCase
import cbr.description.PhraseDescriptionCreators
import instruments.InstrumentType.InstrumentType
import representation.Phrase
import storage.KDTreeIndex
import training.TrainingUtils
import utils.IOUtils
import utils.collections.CollectionUtils

object DemoPopulateDB {
  def run(resourceDirPath: String): Unit = {
    val filenames = IOUtils.filesInDir(IOUtils.getResourcePath(resourceDirPath)).getOrElse(List())
    val index = KDTreeIndex.loadOrCreateDefault[MusicalCase](PhraseDescriptionCreators.getDefault)
    index.clear().compact()
    filenames.foreach(TrainingUtils.addCasesToIndex(index, _))
    index.save()
  }
}
