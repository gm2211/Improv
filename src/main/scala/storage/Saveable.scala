package storage

import scala.util.Try

trait Saveable {
  /**
   * Serialise the instance that implements this interface to file.
   * If no path is provided, the implementation will attempt to serialise from the file from which it was loaded
   * @param path Path of the file to which the instance should be serialised
   * @return
   */
  def save(path: Option[String] = None): Try[Boolean]
}
