package utils

import java.io._

import com.google.common.io.{ByteStreams, Files}
import utils.ImplicitConversions.{toGuavaFunction, toGuavaPredicate}

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}

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
        .filter((file: File) => file.isFile)
        .transform((file: File) => file.getPath)
        .toList
        .toList
    }
  }

  def getBufferedReader(path: String) = {
    Try(new BufferedReader(new InputStreamReader(new FileInputStream(path))))
  }

  def getBufferedWriter(path: String) = {
    Try(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path))))
  }

  def getFilename(fullPath: String): String =
    Files.getNameWithoutExtension(fullPath)
}
