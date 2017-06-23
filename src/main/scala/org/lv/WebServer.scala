package org.lv

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import org.lv.oversee.domain._

import scala.io.StdIn
import scala.util.Success

object WebServer {
  def main(args: Array[String]) {

    implicit val system = ActorSystem("oversee-api")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher
    val buildingRepository = new InMemoryBuildingRepository()

    val route =
      path("buildings" / """\w+""".r / "badges" / """\w+""".r) { (buildingId, badgeId) =>
        get {
          val handler = new CheckIn(buildingRepository)
          val responseText = handler.handle(BuildingId.fromString(buildingId).get, BadgeId.fromString(badgeId).get) match {
            case Success(Right(badgeCheckedIn)) => "accepted"
            case Success(Left(accessRefused)) => "refused"
            case _ => "error"
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
