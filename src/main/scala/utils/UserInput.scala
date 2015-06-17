package utils

import utils.collections.CollectionUtils

object UserInput {
  val SONG_RESOURCE_DIR: String = "trainingMIDIs"

  def chooseASong(songDir: String = SONG_RESOURCE_DIR): String = {
    val songs = IOUtils.filesInDir(IOUtils.getResourcePath(songDir)).get.map(IOUtils.getFilename)
    println("Available songs:")
    CollectionUtils.printNewLine(songs)
    println()

    val resource = scala.io.StdIn.readLine("Choose a song: ")
    IOUtils.getResourcePath(s"$songDir/$resource.mid")
  }

}
