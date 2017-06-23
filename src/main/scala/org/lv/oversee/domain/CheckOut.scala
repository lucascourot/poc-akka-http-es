package org.lv.oversee.domain

import scala.util.Try

final class CheckOut(buildingRepository: BuildingRepository) {
  def getAggregate(buildingId: BuildingId): Try[Building] = EntityNotFoundRepository.withNotFound(buildingRepository.get)(buildingId)

  def handle(buildingId: BuildingId, badgeId: BadgeId): Try[Either[BadgeCheckedOutAgain, BadgeCheckedOut]] = {
    getAggregate(buildingId).map { building =>
      val checkoutEvent = building.checkOut(badgeId)

      buildingRepository.save(building)

      checkoutEvent
    }
  }
}
