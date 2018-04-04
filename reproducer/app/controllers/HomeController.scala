package controllers

import play.api.mvc._
import akka.stream.Materializer

import scala.concurrent.Future
import akka.actor._
import play.api.libs.json._
import play.api.libs.streams.ActorFlow

import scala.concurrent.ExecutionContext

object MyWebSocketActor2 {
  def props(cookie: Option[String], token: Option[String], headers: Headers)(out: ActorRef) = Props(new MyWebSocketActor2(cookie, token, headers, out))
}

class MyWebSocketActor2(cookie: Option[String], token: Option[String], headers: Headers, out: ActorRef) extends Actor {
  def receive = {
    case msg: String =>
      out ! s"I received WS message: $msg with cookie: $cookie, auth token: $token\nheaders: $headers"
  }
}

class HomeController(cc: ControllerComponents)(implicit ec: ExecutionContext, system: ActorSystem, materializer: Materializer)
  extends AbstractController(cc) {

  val logger = play.api.Logger(getClass)

  def get = Action { request =>
    logger.info(s"Request Session in ${request.session}")
    logger.info(s"Request Headers in ${request.headers}")
    logger.info(s"Request Cookies in ${request.cookies}")
    val cookie = request.headers.get("Set-Cookie")
    val token = request.headers.get("X-Auth-Token")
    Ok(s"I received GET request with cookie: $cookie, auth token: $token\nheaders: ${request.headers}")
  }

  /**
    * Creates a websocket.  `acceptOrResult` is preferable here because it returns a
    * Future[Flow], which is required internally.
    *
    * @return a fully realized websocket.
    */
  def ws: WebSocket = WebSocket.acceptOrResult[String, String] {
    rh: RequestHeader =>
      {
        logger.info(s"Request Session in ${rh.session}")
        logger.info(s"Request Headers in ${rh.headers}")
        logger.info(s"Request Cookies in ${rh.cookies}")
        logger.info(s"Request in ${rh}")
        val cookie = rh.headers.get("Set-Cookie")
        val token = rh.headers.get("X-Auth-Token")

        Future.successful(ActorFlow.actorRef { out =>
          MyWebSocketActor2.props(cookie, token, rh.headers)(out)
        }).map { flow =>
          Right(flow)
        }.recover {
          case e: Exception =>
            logger.error("Cannot create websocket", e)
            val jsError = Json.obj("error" -> "Cannot create websocket")
            val result = InternalServerError(jsError)
            Left(result)
        }
      }
  }
}