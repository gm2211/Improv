package web

import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{SessionSupport, ScalatraServlet}
import org.scalatra.atmosphere.AtmosphereSupport
import org.scalatra.json.{JacksonJsonSupport, JValueResult}
import org.scalatra.scalate.ScalateSupport

class AssetsController extends ScalatraServlet with JValueResult
  with ScalateSupport
  with JacksonJsonSupport
  with SessionSupport
  with AtmosphereSupport {
  override protected implicit def jsonFormats: Formats = DefaultFormats

  get("/views/:page") {
    contentType="text/html"
    jade(s"/views/${params("page")}.jade", "layout" -> "")
  }

}
