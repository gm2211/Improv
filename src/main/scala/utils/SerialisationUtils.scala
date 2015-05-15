package utils

import java.io.ByteArrayInputStream

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

import scala.util.Try

object SerialisationUtils {
  private val mapper = new ObjectMapper() with ScalaObjectMapper

  mapper
    .registerModule(DefaultScalaModule)
    .setVisibility(PropertyAccessor.ALL, Visibility.ANY)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)


  def serialise[A](obj: A): Try[Array[Byte]] = Try {
    mapper.writeValueAsBytes(obj)
  }

  def serialise[A](obj: A, filename: String): Try[Boolean] =
    serialise(obj).flatMap(IOUtils.write(filename, _))

  def deserialise[A: Manifest](bytes: Array[Byte]): Try[A] = {
    val stream = new ByteArrayInputStream(bytes)
    Try {
      mapper.readValue[A](stream)
    }
  }

  def deserialise[A: Manifest](bytes: Array[Byte], a: Class[A]): Try[A] = {
    val stream = new ByteArrayInputStream(bytes)
    Try {
      mapper.readValue(stream, a)
    }
  }

  def deserialise[A: Manifest](filename: String, a: Class[A]): Try[A] =
    IOUtils.read(filename).flatMap { case bytes => deserialise(bytes, a) }

  def deserialise[A: Manifest](filename: String): Try[A] =
    IOUtils.read(filename).flatMap { case bytes => deserialise(bytes) }
}
