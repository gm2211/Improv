package web

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ DefaultServlet, ServletContextHandler }
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

object WebGui extends App {
  val port = if (System.getenv("PORT") != null) System.getenv("PORT").toInt else 8080

  val server = new Server(port)
  val context = new WebAppContext()

  context.setInitParameter(ScalatraListener.LifeCycleKey,
    "web.WebGuiBootstrap")

  context setContextPath "/"
  context.setResourceBase("src/main/resources/webapp")
  context.addEventListener(new ScalatraListener)
  server.setHandler(context)

  server.start()
  server.join()
}
