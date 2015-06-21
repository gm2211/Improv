package web

import java.util.Date

import actors.Orchestra
import actors.monitors.MusicianObserver
import actors.monitors.MusicianObserver.NewMessageEvent
import designPatterns.observer.{EventNotification, Observer}
import org.json4s.JsonDSL._
import org.json4s.{DefaultFormats, Formats, _}
import org.scalatra.atmosphere._
import org.scalatra.json.{JValueResult, JacksonJsonSupport}
import org.scalatra.scalate.ScalateSupport
import org.scalatra.{ScalatraServlet, SessionSupport}


class WebGuiController(orchestra: Orchestra) extends ScalatraServlet
    with JValueResult
    with ScalateSupport
    with JacksonJsonSupport
    with SessionSupport
    with Observer
    with AtmosphereSupport {
  override protected implicit def jsonFormats: Formats = DefaultFormats
  implicit val system = orchestra.system
  implicit val executionContext = orchestra.system.dispatcher
  var connected = false

  orchestra.registerMusician((_) => MusicianObserver(this))

  orchestra.start()

  val atmosphereClient = new AtmosphereClient {
      def receive: AtmoReceive = {
        case Connected =>
          println("Client %s is connected" format uuid)
          connected = true
          broadcast(("author" -> "Someone") ~ ("message" -> "joined the room") ~ ("time" -> new Date().getTime.toString), Everyone)
//
//        case Disconnected(ClientDisconnected, _) =>
//          broadcast(("author" -> "Someone") ~ ("message" -> "has left the room") ~ ("time" -> new Date().getTime.toString), Everyone)
//
//        case Disconnected(ServerDisconnected, _) =>
//          println("Server disconnected the client %s" format uuid)
//        case _: TextMessage =>
//          send(("author" -> "system") ~ ("message" -> "Only json is allowed") ~ ("time" -> new Date().getTime.toString))
//
//        case JsonMessage(json) =>
//          println("Got message %s from %s".format((json \ "message").extract[String], (json \ "author").extract[String]))
//          val msg = json merge ("time" -> new Date().getTime.toString: JValue)
//          broadcast(msg) // by default a broadcast is to everyone but self
//        //  send(msg) // also send to the sender
        case _ =>
      }
    }

  get("/") {
    contentType="text/html"
    ssp("/index")
  }


  atmosphere("/the-chat") {
    atmosphereClient
  }

  error {
    case t: Throwable => t.printStackTrace()
  }

  notFound {
    // remove content type in case it was set through an action
    contentType = null
    // Try to render a ScalateTemplate if no route matched
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound()
  }

  override def notify(eventNotification: EventNotification): Unit = eventNotification match {
    case messageEvent: NewMessageEvent =>
      if (connected) {
        println(s"broadcasting ${messageEvent.message}")
        atmosphereClient.broadcast(messageEvent.message.toString)
      }
    case _ =>
  }
}
