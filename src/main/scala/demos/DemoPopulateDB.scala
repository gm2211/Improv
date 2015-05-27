package demos

import cbr.description.PhraseDescriptionCreators
import representation.Phrase
import storage.KDTreeIndex
import training.TrainingUtils
import utils.IOUtils
import utils.collections.CollectionUtils

object DemoPopulateDB {
  def run(resourceDirPath: String): Unit = {
    val index = KDTreeIndex.loadOrCreateDefault[Phrase](PhraseDescriptionCreators.getDefault)
    val filenames = IOUtils.filesInDir(IOUtils.getResourcePath(resourceDirPath)).getOrElse(List())
    filenames.foreach(TrainingUtils.addCasesToIndex(index, _))
    index.save()
  }
}
