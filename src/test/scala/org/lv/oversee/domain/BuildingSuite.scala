package org.lv.oversee.domain

import org.scalatest.FunSuite

class BuildingSuite extends FunSuite{
  test("Building should check in badge id") {
    // Given
    val building = new Building(BuildingId.fromString("A").get)
    val badgeId = BadgeId.fromString("B").get

    // When
    building.checkIn(badgeId)

    // Then
    assert(building.getRecordedEvents.length == 1)
    assert(building.getRecordedEvents(0).isInstanceOf[BadgeCheckedIn])
  }

  test("Building should not check in twice same badge id") {
    // Given
    val building = new Building(BuildingId.fromString("A").get)
    val badgeId = BadgeId.fromString("B").get

    // When
    building.checkIn(badgeId)
    building.checkIn(badgeId)

    // Then
    assert(building.getRecordedEvents.length == 2)
    assert(building.getRecordedEvents(0).isInstanceOf[BadgeCheckedIn])
    assert(building.getRecordedEvents(1).isInstanceOf[AccessRefused])
  }
}
