package web

import javax.servlet.ServletContext

import actors.Orchestra
import actors.directors.WaitingDirector
import org.scalatra.LifeCycle

class WebGuiBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    val orchestra: Orchestra = Orchestra.builder
      .withName("Improv")
//      .withDirector(WaitingDirector.builder)
      .build
    
    context.mount(new WebGuiController(orchestra), "/api/*")
    context.mount(new WebGuiController(orchestra), "/assets/*")
  }
}
