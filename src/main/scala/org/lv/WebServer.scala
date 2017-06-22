package org.lv

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import org.lv.oversee.domain.BadgeId

import scala.io.StdIn
import scala.util.{Failure, Success}

object WebServer {
  def main(args: Array[String]) {

    implicit val system = ActorSystem("oversee-api")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val route =
      path("buildings" / """\w+""".r / "badges" / """\w+""".r) { (buildingId, badgeId) =>
        get {
          val responseText = BadgeId.fromString(badgeId) match {
            case Success(badge) => s"<h1>Hello ${badge.toString}</h1>"
            case Failure(exception) => s"<h1>Error: ${exception.getMessage}</h1>"
          }

          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, responseText))
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
