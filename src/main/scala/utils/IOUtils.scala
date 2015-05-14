package utils

import java.io.{FileInputStream, FileOutputStream}

import com.google.common.io.ByteStreams

import scala.util.{Failure, Success, Try}

object IOUtils {
  def write(filename: String, bytes: Array[Byte]): Try[Boolean] = {
    var out: Option[FileOutputStream] = None
    try {
      out = Some(new FileOutputStream(filename))
      out.get.write(bytes)
      Success(true)
    } catch {
      case ignored: Throwable =>
        Failure(ignored)
    } finally {
      out.foreach(_.close)
    }
  }

  def read(filename: String): Try[Array[Byte]] = {
    var in: Option[FileInputStream] = None
    try {
      in = Some(new FileInputStream(filename))
      Success(ByteStreams.toByteArray(in.get))
    } catch {
      case ignored: Throwable =>
        Failure(ignored)
    } finally {
      in.foreach(_.close)
    }
  }

  def getResourcePath(resourceName: String): String =
    ClassLoader.getSystemClassLoader.getResource(resourceName).getPath
}
