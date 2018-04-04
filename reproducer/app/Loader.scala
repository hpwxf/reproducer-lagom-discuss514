import akka.actor.ActorSystem
import com.lightbend.lagom.scaladsl.api.{ LagomConfigComponent, ServiceAcl, ServiceInfo }
import com.lightbend.lagom.scaladsl.client.LagomServiceClientComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.softwaremill.macwire._
import controllers.HomeController
import play.api.ApplicationLoader.Context
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.routing.Router
import play.api.{ Application, ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator }
import play.filters.HttpFiltersComponents
import router.Routes
import scala.collection.immutable

class PlayApp(context: Context) extends BuiltInComponentsFromContext(context)
  with AhcWSComponents
  with HttpFiltersComponents {
  override lazy val router: Router = {
    val prefix: String = "/"
    wire[Routes]
  }

  implicit private lazy val as: ActorSystem = actorSystem
  private lazy val homeController = wire[HomeController]
}

abstract class LagomApp(context: Context) extends BuiltInComponentsFromContext(context)
  with AhcWSComponents
  with LagomConfigComponent
  with HttpFiltersComponents
  with LagomServiceClientComponents {

  override lazy val serviceInfo = ServiceInfo(
    "web-gateway",
    Map(
      "web-gateway" -> immutable.Seq(ServiceAcl.forPathRegex("(?!/api/).*"))
    )
  )

  override lazy val router: Router = {
    val prefix: String = "/"
    wire[Routes]
  }

  implicit private lazy val as: ActorSystem = actorSystem
  private lazy val homeController = wire[HomeController]
}

class Loader extends ApplicationLoader with utils.Logger {
  override def load(context: Context): Application = {

    LoggerConfigurator (context.environment.classLoader).foreach {
      _.configure (context.environment)
    }

    val lagomMode = context.initialConfiguration.getOptional[Boolean]("config.lagomMode").getOrElse(true)

    logger.info(s"Starting with Lagom Mode = $lagomMode")

    if (lagomMode)
      (new LagomApp(context) with LagomDevModeComponents).application
    else
      new PlayApp(context).application
  }
}
