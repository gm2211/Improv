package demos

import cbr.MusicalCase
import cbr.description.PhraseDescriptionCreators
import designPatterns.observer.{EventNotification, Observer}
import instruments.JFugueUtils
import midi.{JMusicMIDIParser, MIDIPlayer}
import training.DefaultMusicalCaseExtractor
import training.ann.ANNTrainingData

import scala.util.Try

object DemoCreateTrainingDataANN extends Observer with App {
  def run(filename: String, fromScratch: Boolean = false) = {
    val extractor = new DefaultMusicalCaseExtractor(JMusicMIDIParser)

    val cases = extractor.getCases(filename)
    val player = new MIDIPlayer()
    var continue = true

    player.addObserver(this)

    val descriptionCreator = PhraseDescriptionCreators.getDefault
    val db = ANNTrainingData.loadDefaultDB
    val trainingData = db.get(filename).getOrElse(new ANNTrainingData(descriptionCreator.getMaxDescriptionSize, 1))
    if (fromScratch) trainingData.clear()

    for ((_, sol) <- cases.drop(trainingData.dataPointsCount).toStream.takeWhile( _ => continue)) {
      val seq = JFugueUtils.toSequence(sol.phrase, sol.instrumentType)
      player.play(seq)

      this.synchronized {
        while (player.playing) {
          wait()
        }
      }

      print("Rating: ")
      val rating = Try(scala.io.StdIn.readDouble())
      player.stop()
      val features = descriptionCreator.createCaseDescription(MusicalCase(sol.instrumentType, sol.phrase))
      val sig = features.getSignature

      rating.foreach(ratingVal => trainingData.addDataPoint(sig, Array(ratingVal)))
      trainingData.normalised.saveCSV("/tmp/dummy.csv")

      db.put(filename, trainingData)
      continue = scala.io.StdIn.readLine("Continue[y/n]: ").toLowerCase != "n"
    }
    db.commit()
  }

  override def notify(eventNotification: EventNotification): Unit = {
    this.synchronized(notifyAll())
  }
}
