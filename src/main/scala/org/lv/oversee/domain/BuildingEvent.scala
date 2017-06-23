package org.lv.oversee.domain

case class AccessRefused(buildingId: BuildingId, badgeId: BadgeId, message: String) extends DomainEvent[BuildingId] {
  override def getAggregateId: BuildingId = buildingId
}

case class BadgeCheckedIn(buildingId: BuildingId, badgeId: BadgeId) extends DomainEvent[BuildingId] {
  override def getAggregateId: BuildingId = buildingId
}
