package demos

import cbr.MusicalCase
import cbr.description.PhraseDescriptionCreators
import instruments.InstrumentType.InstrumentType
import representation.Phrase
import storage.KDTreeIndex
import training.SystemTrainer
import utils.IOUtils
import utils.collections.CollectionUtils

object DemoPopulateDB {
  def run(resourceDirPath: String): Unit = {
    val dirPath = IOUtils.getResourcePath(resourceDirPath)
    val filenames = IOUtils.filesInDir(dirPath).getOrElse(List())
    val index = KDTreeIndex.loadOrCreateDefault[MusicalCase](PhraseDescriptionCreators.getDefault)
    val trainer = new SystemTrainer(index = index)
    trainer.addCasesToIndex(filenames)
  }
}

