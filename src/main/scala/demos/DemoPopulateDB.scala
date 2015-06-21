package demos

import cbr.MusicalCase
import cbr.description.PhraseDescriptionCreators
import com.fasterxml.jackson.databind.ObjectMapper
import instruments.InstrumentType.PIANO
import net.sf.javaml.core.kdtree.KDTree
import representation.Phrase
import storage.KDTreeIndex
import training.SystemTrainer
import utils.{SerialisationUtils, IOUtils}

object DemoPopulateDB extends App {
  run("trainingMIDIs")
  def run(resourceDirPath: String): Unit = {
    val dirPath = IOUtils.getResourcePath(resourceDirPath)
    val filenames = IOUtils.filesInDir(dirPath).getOrElse(List()).take(1)
    val index = KDTreeIndex.loadOrCreateDefault[MusicalCase](PhraseDescriptionCreators.getDefault)
    val trainer = new SystemTrainer(index = index)
    trainer.addCasesToIndex(filenames)
    println(index.size)
    index.save()
  }
}

