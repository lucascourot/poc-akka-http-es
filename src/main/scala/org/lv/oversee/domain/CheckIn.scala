package org.lv.oversee.domain

import scala.util.Try

final class CheckIn(buildingRepository: BuildingRepository) {
  def getAggregate(buildingId: BuildingId): Try[Building] = EntityNotFoundRepository.withNotFound(buildingRepository.get)(buildingId)

  def handle(buildingId: BuildingId, badgeId: BadgeId): Try[Either[AccessRefused, BadgeCheckedIn]] = {
    getAggregate(buildingId).map { building =>
      val acceptedOrRefused = building.checkIn(badgeId)

      buildingRepository.save(building)

      acceptedOrRefused
    }
  }
}
