package demos

import cbr.MusicalCase
import cbr.description.PhraseDescriptionCreators
import designPatterns.observer.{EventNotification, Observer}
import instruments.JFugueUtils
import midi.{JMusicMIDIParser, MIDIPlayer}
import training.DefaultMusicalCaseExtractor
import training.ann.ANNTrainingData
import utils.IOUtils
import utils.collections.CollectionUtils

import scala.util.Try

object DemoCreateTrainingDataANN extends Observer with App {
  val DEFAULT_DB_PATH: String = ANNTrainingData.DEFAULT_DB_PATH
  val DEFAULT_SAVE_PATH: String = "/tmp/annData"

  val resource = scala.io.StdIn.readLine("Enter music filename: ")

  val savePath = {
    val path = scala.io.StdIn.readLine(s"Enter save path for output file[default:$DEFAULT_SAVE_PATH]: ")
    Option(path).filter(_.trim.nonEmpty).getOrElse(DEFAULT_SAVE_PATH)
  }

  val dbPath = {
    val path = scala.io.StdIn.readLine(s"Choose dbPath [default:$DEFAULT_DB_PATH]: ")
    Option(path).filter(_.trim.nonEmpty).getOrElse(DEFAULT_DB_PATH)
  }

  val filename = IOUtils.getResourcePath(s"trainingMIDIs/$resource.mid")

  run(filename, savePath, dbPath)
  def run(
      filename: String,
      savePath: String = DEFAULT_SAVE_PATH,
      dbLocation: String = DEFAULT_DB_PATH,
      fromScratch: Boolean = false) = {
    val extractor = new DefaultMusicalCaseExtractor(JMusicMIDIParser)

    val cases = extractor.getCases(filename)
    val player = new MIDIPlayer()
    var continue = true

    player.addObserver(this)

    val descriptionCreator = PhraseDescriptionCreators.getDefault
    println(s"DB location: $dbLocation")
    val db = ANNTrainingData.loadDB(dbLocation)
    println("DB content: ")
    CollectionUtils.print(db.keySet())
    val trainingData = db.get(filename).getOrElse(new ANNTrainingData(descriptionCreator.getMaxDescriptionSize, 1))

    println(s"Starting from melody #${trainingData.dataPointsCount} out of ${cases.size}")

    if (fromScratch) trainingData.clear()

    for ((_, sol) <- cases.toStream.drop(trainingData.dataPointsCount).takeWhile( _ => continue)) {
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
      val features = descriptionCreator.createCaseDescription(MusicalCase(sol.instrumentType, phrase = sol.phrase))
      val sig = features.getSignature

      rating.foreach(ratingVal => trainingData.addDataPoint(sig, Array(ratingVal)))
      trainingData.saveCSV(s"$savePath.csv")

      continue = scala.io.StdIn.readLine("Continue[y/n]: ").toLowerCase != "n"
    }

    db.put(filename, trainingData)
    db.commit()
  }

  override def notify(eventNotification: EventNotification): Unit = {
    this.synchronized(notifyAll())
  }
}
