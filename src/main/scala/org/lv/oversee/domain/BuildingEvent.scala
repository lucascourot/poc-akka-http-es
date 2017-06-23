package org.lv.oversee.domain

case class AccessRefused(buildingId: BuildingId, badgeId: BadgeId, reason: String) extends DomainEvent[BuildingId] {
  def getAggregateId: BuildingId = buildingId
}

case class BadgeCheckedIn(buildingId: BuildingId, badgeId: BadgeId) extends DomainEvent[BuildingId] {
  def getAggregateId: BuildingId = buildingId
}

case class BadgeCheckedOut(buildingId: BuildingId, badgeId: BadgeId) extends DomainEvent[BuildingId] {
  def getAggregateId: BuildingId = buildingId
}

case class BadgeCheckedOutAgain(buildingId: BuildingId, badgeId: BadgeId) extends DomainEvent[BuildingId] {
  def getAggregateId: BuildingId = buildingId
}
