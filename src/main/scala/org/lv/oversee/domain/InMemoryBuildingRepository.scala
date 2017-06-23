package org.lv.oversee.domain

import scala.collection.mutable
import scala.util.{Success, Try}

final class InMemoryBuildingRepository extends BuildingRepository {
  val eventStore: mutable.HashMap[BuildingId, List[DomainEvent[BuildingId]]] = new mutable.HashMap[BuildingId, List[DomainEvent[BuildingId]]]()

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