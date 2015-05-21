package utils

import java.io.{RandomAccessFile, FileInputStream, FileOutputStream}

import com.google.common.io.ByteStreams

import scala.util.{Failure, Success, Try}

object IOUtils {
  val SYNCHRONOUS_RW = "rws"

  def deleteContent(path: String): Boolean = {
    Try{
      val file = new RandomAccessFile(path, SYNCHRONOUS_RW)
      file.setLength(0)
      file.close()
    }.isSuccess
  }

  def write(path: String, bytes: Array[Byte]): Try[Boolean] = {
    var out: Option[FileOutputStream] = None
    try {
      out = Some(new FileOutputStream(path))
      out.get.write(bytes)
      Success(true)
    } catch {
      case ignored: Throwable =>
        Failure(ignored)
    } finally {
      out.foreach(_.close)
    }
  }

  def read(path: String): Try[Array[Byte]] = {
    var in: Option[FileInputStream] = None
    try {
      in = Some(new FileInputStream(path))
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
