package org.lv.oversee.domain

import scala.util.{Failure, Try}

final class Open(buildingRepository: BuildingRepository) {
  def handle(buildingId: BuildingId): Try[BuildingOpen] = {
    buildingRepository.get(buildingId).map(_ => Failure(new Error("Already Open"))).getOrElse {
      val building = new Building(buildingId)
      val buildingOpen = building.open()

      buildingRepository.save(building)

      buildingOpen
    }
  }
}
