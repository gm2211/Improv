package web

import actors.Orchestra
import org.json4s.{JValue, DefaultFormats, Formats}
import org.scalatra.atmosphere._
import org.scalatra.json.{JValueResult, JacksonJsonSupport}
import org.scalatra.{ScalatraServlet, SessionSupport}


class WebGui(orchestra: Orchestra) extends ScalatraServlet
    with JValueResult
    with JacksonJsonSupport
    with SessionSupport
    with AtmosphereSupport {
  override protected implicit def jsonFormats: Formats = DefaultFormats
  implicit val executionContext = orchestra.system.dispatcher

  atmosphere("/the-chat") {
    new AtmosphereClient {
      def receive = {
        case Connected =>
        case Disconnected(disco, Some(error)) =>
        case Error(Some(error)) =>
        case TextMessage(text) => send("ECHO: " + text)
        case JsonMessage(json) => broadcast(json)
      }
    }
  }

  override def render(value: JValue)(implicit formats: Formats): JValue = value
}
