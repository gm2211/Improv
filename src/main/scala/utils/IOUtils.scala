package utils

import java.io.{File, FileInputStream, FileOutputStream, RandomAccessFile}

import com.google.common.io.{Files, ByteStreams}

import scala.util.{Failure, Success, Try}
import utils.ImplicitConversions.toGuavaFunction
import scala.collection.JavaConversions._

object IOUtils {
  private val SYNCHRONOUS_RW = "rws"

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

  def filesInDir(dirPath: String): Try[List[String]] = {
    Try{
      val dir = new File(dirPath)
      Files.fileTreeTraverser()
        .breadthFirstTraversal(dir)
        .transform((file: File) => file.getPath)
        .toList
        .toList
    }
  }
}
