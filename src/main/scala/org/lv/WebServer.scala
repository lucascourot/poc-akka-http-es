package org.lv

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import org.lv.oversee.domain._

import scala.io.StdIn
import scala.util.{Failure, Success}

object WebServer {
  def main(args: Array[String]) {

    implicit val system = ActorSystem("oversee-api")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher
    val eventStore = new InMemoryEventStore[BuildingId]
    val buildingRepository = new InMemoryBuildingRepository(eventStore)

    val handler = new Open(buildingRepository)
    handler.handle(BuildingId("abc"))

    val route =
      path("buildings" / """\w+""".r / "badges" / """\w+""".r / "checkin") { (buildingId, badgeId) =>
        get {
          val handler = new CheckIn(buildingRepository)
          val responseMessage = handler.handle(BuildingId.fromString(buildingId).get, BadgeId.fromString(badgeId).get) match {
            case Success(Right(badgeCheckedIn)) => "checked in"
            case Success(Left(accessRefused)) => "access refused"
            case Failure(e) => e.getMessage
          }

          complete(HttpEntity(ContentTypes.`application/json`, s"""{"message":"$responseMessage"}"""))
        }
      } ~
        path("buildings" / """\w+""".r / "badges" / """\w+""".r / "checkout") { (buildingId, badgeId) =>
          get {
            val handler = new CheckOut(buildingRepository)
            val responseMessage = handler.handle(BuildingId.fromString(buildingId).get, BadgeId.fromString(badgeId).get) match {
              case Success(Right(badgeCheckedOut)) => "checked out"
              case Success(Left(badgeCheckedOutAgain)) => "checked out again"
              case _ => "error"
            }

            complete(HttpEntity(ContentTypes.`application/json`, s"""{"message":"$responseMessage"}"""))
          }
        } ~
        path("log" / """\w+""".r) { (buildingId) =>
          get {
            val list = eventStore.load(StreamName(buildingId)) match {
              case Success(stream) => {
                stream.map { stream =>
                  stream.streamEvents.map(event => s"<li>${event.getClass}</li>")
                }.getOrElse(List(s"<ul></ul>"))
              }
              case _ => List(s"<p>error</p>")
            }
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"""${list.mkString}"""))
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
