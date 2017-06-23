package org.lv.oversee.domain

import scala.collection.mutable.ListBuffer

final class Building(val buildingId: BuildingId) {
  private lazy val peopleInTheBuilding: ListBuffer[BadgeId] = ListBuffer()
  private lazy val events: ListBuffer[DomainEvent[BuildingId]] = ListBuffer()

  def checkIn(badgeId: BadgeId): Either[AccessRefused, BadgeCheckedIn] = {
    val event = if (peopleInTheBuilding.contains(badgeId)) AccessRefused(buildingId, badgeId, "Already checked in")
    else BadgeCheckedIn(buildingId, badgeId)

    recordThat(event) match {
      case badgeCheckedIn: BadgeCheckedIn => Right(badgeCheckedIn)
      case accessRefused: AccessRefused => Left(accessRefused)
      case _ => Left(AccessRefused(buildingId, badgeId, "Strange things happen"))
    }
  }

  private def recordThat(domainEvent: DomainEvent[BuildingId]): DomainEvent[BuildingId] = {
    events.append(domainEvent)

    apply(domainEvent)
  }

  private def apply(domainEvent: DomainEvent[BuildingId]): DomainEvent[BuildingId] = {
    domainEvent match {
      case domainEvent: BadgeCheckedIn => addPersonToTheBuilding(domainEvent)
      case _ => domainEvent
    }
  }

  private def addPersonToTheBuilding(badgeCheckedIn: BadgeCheckedIn): BadgeCheckedIn = {
    peopleInTheBuilding.append(badgeCheckedIn.badgeId)

    badgeCheckedIn
  }

  def reconstituteFromHistory(events: List[DomainEvent[BuildingId]]): Building = {
    events.foreach(event => apply(event))

    this
  }

  def getRecordedEvents: List[DomainEvent[BuildingId]] = events.toList
}
