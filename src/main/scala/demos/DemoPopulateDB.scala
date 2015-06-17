package demos

import cbr.MusicalCase
import cbr.description.PhraseDescriptionCreators
import storage.KDTreeIndex
import training.SystemTrainer
import utils.IOUtils

object DemoPopulateDB extends App {
  run("trainingMIDIs")
  def run(resourceDirPath: String): Unit = {
    val dirPath = IOUtils.getResourcePath(resourceDirPath)
    val filenames = IOUtils.filesInDir(dirPath).getOrElse(List())
    val index = KDTreeIndex.loadOrCreateDefault[MusicalCase](PhraseDescriptionCreators.getDefault)
    index.clear()
    val trainer = new SystemTrainer(index = index)
    trainer.addCasesToIndex(filenames)
  }
}

