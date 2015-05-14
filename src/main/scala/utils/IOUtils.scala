package utils

import java.io.{FileInputStream, FileOutputStream}

import com.google.common.io.ByteStreams

object IOUtils {
  def write(filename: String, bytes: Array[Byte]): Boolean = {
    var out: Option[FileOutputStream] = None
    try {
      out = Some(new FileOutputStream(filename))
      out.get.write(bytes)
      true
    } catch {
      case ignored: Throwable =>
        false
    } finally {
      out.foreach(_.close)
    }
  }

  def read(filename: String): Option[Array[Byte]] = {
    var in: Option[FileInputStream] = None
    try {
      in = Some(new FileInputStream(filename))
      Some(ByteStreams.toByteArray(in.get))
    } catch {
      case ignored: Throwable =>
        None
    } finally {
      in.foreach(_.close)
    }
  }

  def getResourcePath(resourceName: String): String =
    ClassLoader.getSystemClassLoader.getResource(resourceName).getPath
}
