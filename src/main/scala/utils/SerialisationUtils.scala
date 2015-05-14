package utils

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import com.twitter.chill.{Input, Output, ScalaKryoInstantiator}

import scala.util.Try

object SerialisationUtils {
  private val serialiser = new ScalaKryoInstantiator().newKryo()

  def serialise[A](obj: A): Try[Array[Byte]]= {
    val stream = new ByteArrayOutputStream()
    val output = new Output(stream)
    Try {
      serialiser.writeClassAndObject(output, obj)
      output.getBuffer
    }
  }

  def serialise[A](obj: A, filename: String): Try[Boolean] =
    serialise(obj).flatMap(IOUtils.write(filename, _))

  def deserialise[A](bytes: Array[Byte]): Try[A] = {
    val stream = new ByteArrayInputStream(bytes)
    val input = new Input(stream)
    Try { serialiser.readClassAndObject(input).asInstanceOf[A] }
  }

  def deserialise[A](filename: String): Try[A] =
    IOUtils.read(filename).flatMap{ case bytes => deserialise(bytes) }
}
