package org.lv.oversee.domain

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

final case class BadgeId(id: String) {
  override def toString: String = id
}

object BadgeId {
  def fromString(id: String): Try[BadgeId] = {
    if (id.length() > 50) Failure(InvalidBadgeId(s"$id is invalid"))
    else Success(BadgeId(id))
  }
}

case class InvalidBadgeId(message: String) extends Exception(message)

final case class BuildingId(id: String) {
  override def toString: String = id
}

object BuildingId {
  def fromString(id: String): Try[BuildingId] = {
    if (id.length() > 50) Failure(InvalidBuildingId(s"$id is invalid"))
    else Success(BuildingId(id))
  }
}

case class InvalidBuildingId(message: String) extends Exception(message)

trait DomainEvent[T] {
  def getAggregateId: T
}

case class AccessRefused(buildingId: BuildingId, badgeId: BadgeId, message: String) extends DomainEvent[BuildingId] {
  override def getAggregateId: BuildingId = buildingId
}

case class BadgeCheckedIn(buildingId: BuildingId, badgeId: BadgeId) extends DomainEvent[BuildingId] {
  override def getAggregateId: BuildingId = buildingId
}

final class Building(val buildingId: BuildingId) {
  private lazy val peopleInTheBuilding: ListBuffer[BadgeId] = ListBuffer()
  private lazy val events: ListBuffer[DomainEvent[BuildingId]] = ListBuffer()

  def checkIn(badgeId: BadgeId): Building = {
    if (peopleInTheBuilding.contains(badgeId)) recordThat(AccessRefused(buildingId, badgeId, "Already checked in"))
    else recordThat(BadgeCheckedIn(buildingId, badgeId))
  }

  private def recordThat(domainEvent: DomainEvent[BuildingId]): Building = {
    events.append(domainEvent)

    apply(domainEvent)
  }

  private def apply(domainEvent: DomainEvent[BuildingId]): Building = {
    domainEvent match {
      case domainEvent: BadgeCheckedIn => peopleInTheBuilding.append(domainEvent.badgeId)
      case _ =>
    }

    this
  }

  def reconstituteFromHistory(events: List[DomainEvent[BuildingId]]): Building = {
    events.foreach(event => apply(event))

    this
  }

  def getRecordedEvents: List[DomainEvent[BuildingId]] = events.toList
}

trait BuildingRepository {
  def get(buildingId: BuildingId): Option[Building]
  def save(building: Building): Try[Building]
}

final class InMemoryBuildingRepository extends BuildingRepository {
  val eventStore : mutable.HashMap[BuildingId, List[DomainEvent[BuildingId]]] = new mutable.HashMap[BuildingId, List[DomainEvent[BuildingId]]]()

  override def get(buildingId: BuildingId): Option[Building] = {
    val aggregateStream = eventStore.getOrElse(buildingId, List())

    val building = new Building(buildingId)

    Some(building.reconstituteFromHistory(aggregateStream))
  }

  override def save(building: Building): Try[Building] = {
    val aggregateStream = eventStore.getOrElse(building.buildingId, List())
    eventStore.put(building.buildingId, aggregateStream ++ building.getRecordedEvents)

    Success(building)
  }
}

object EntityNotFoundRepository {
  def withNotFound[A, B](exec: A => Option[B])(id: A): Try[B] =
    exec(id) match {
      case Some(v) => Try(v)
      case  _ => Failure(new Error("..."))
    }
}

final class CheckIn(buildingRepository: BuildingRepository) {
  def getAggregate(buildingId: BuildingId): Try[Building] = EntityNotFoundRepository.withNotFound(buildingRepository.get)(buildingId)

  def handle(buildingId: BuildingId, badgeId: BadgeId): Try[Building] = {
    getAggregate(buildingId) match {
      case Success (building) => buildingRepository.save(building.checkIn(badgeId))
      case Failure (error) => Failure(error)
    }
  }
}
