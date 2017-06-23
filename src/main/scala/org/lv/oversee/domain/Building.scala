package org.lv.oversee.domain

import scala.collection.mutable

final class Building(val buildingId: BuildingId) extends Aggregate[Building, BuildingId] {
  private lazy val peopleInTheBuilding: mutable.Set[BadgeId] = mutable.Set[BadgeId]()

  def reconstituteFromHistory(events: List[DomainEvent[BuildingId]]): Building = {
    events.foreach(event => apply(event))

    this
  }

  def checkIn(badgeId: BadgeId): Either[AccessRefused, BadgeCheckedIn] = {
    val event = if (peopleInTheBuilding.contains(badgeId)) AccessRefused(buildingId, badgeId, "Already checked in")
    else BadgeCheckedIn(buildingId, badgeId)

    recordThat(event) match {
      case badgeCheckedIn: BadgeCheckedIn => Right(badgeCheckedIn)
      case accessRefused: AccessRefused => Left(accessRefused)
      case _ => Left(AccessRefused(buildingId, badgeId, "Strange things happen"))
    }
  }

  def checkOut(badgeId: BadgeId): Either[BadgeCheckedOutAgain, BadgeCheckedOut] = {
    val event = if (peopleInTheBuilding.contains(badgeId)) BadgeCheckedOut(buildingId, badgeId)
    else BadgeCheckedOutAgain(buildingId, badgeId)

    recordThat(event) match {
      case badgeCheckedOut: BadgeCheckedOut => Right(badgeCheckedOut)
      case badgeCheckedOutAgain: BadgeCheckedOutAgain => Left(badgeCheckedOutAgain)
      case _ => Left(BadgeCheckedOutAgain(buildingId, badgeId))
    }
  }

  def apply(domainEvent: DomainEvent[BuildingId]): DomainEvent[BuildingId] = {
    domainEvent match {
      case domainEvent: BadgeCheckedIn => addPersonToTheBuilding(domainEvent)
      case domainEvent: BadgeCheckedOut => removePersonFromTheBuilding(domainEvent)
      case _ => domainEvent
    }
  }

  private def addPersonToTheBuilding(badgeCheckedIn: BadgeCheckedIn): BadgeCheckedIn = {
    peopleInTheBuilding += badgeCheckedIn.badgeId

    badgeCheckedIn
  }

  private def removePersonFromTheBuilding(badgeCheckedOut: BadgeCheckedOut): BadgeCheckedOut = {
    peopleInTheBuilding -= badgeCheckedOut.badgeId

    badgeCheckedOut
  }
}
