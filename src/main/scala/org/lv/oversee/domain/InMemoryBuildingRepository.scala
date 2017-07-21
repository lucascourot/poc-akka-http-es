package org.lv.oversee.domain

import scala.util.{Failure, Success, Try}

final class InMemoryBuildingRepository(val eventStore: EventStore[BuildingId]) extends BuildingRepository {
  override def get(buildingId: BuildingId): Option[Building] = {
    eventStore.load(StreamName(buildingId.toString)) match {
      case Success(Some(stream)) => {
        val building = new Building(buildingId)

        Some(building.reconstituteFromHistory(stream.streamEvents))
      }
      case Success(_) => None
      case _ => None
    }
  }

  override def save(building: Building): Try[Building] = {
    eventStore.commit(Stream(
      StreamName(building.buildingId.toString),
      building.getRecordedEvents
    )) match {
      case Success(_) => Success(building)
      case Failure(exception) => Failure(exception)
    }
  }
}
