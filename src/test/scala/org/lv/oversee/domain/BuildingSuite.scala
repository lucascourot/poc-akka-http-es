package org.lv.oversee.domain

import org.scalatest.FunSuite

class BuildingSuite extends FunSuite {
  test("Building should check in a badge id") {
    // Given
    val building = new Building(BuildingId.fromString("A").get)
    val badgeId = BadgeId.fromString("B").get

    // When
    building.checkIn(badgeId)

    // Then
    assert(building.getRecordedEvents.length == 1)
    assert(building.getRecordedEvents.head.isInstanceOf[BadgeCheckedIn])
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
    assert(building.getRecordedEvents.head.isInstanceOf[BadgeCheckedIn])
    assert(building.getRecordedEvents(1).isInstanceOf[AccessRefused])
  }

  test("Building should check out a badge id") {
    // Given
    val building = new Building(BuildingId.fromString("A").get)
    val badgeId = BadgeId.fromString("B").get

    // When
    building.checkIn(badgeId)
    building.checkOut(badgeId)

    // Then
    assert(building.getRecordedEvents.length == 2)
    assert(building.getRecordedEvents(1).isInstanceOf[BadgeCheckedOut])
  }

  test("Building should check out again a badge id that is no more inside the building") {
    // Given
    val building = new Building(BuildingId.fromString("A").get)
    val badgeId = BadgeId.fromString("B").get

    // When
    building.checkIn(badgeId)
    building.checkOut(badgeId)
    building.checkOut(badgeId)

    // Then
    assert(building.getRecordedEvents.length == 3)
    assert(building.getRecordedEvents(1).isInstanceOf[BadgeCheckedOut])
    assert(building.getRecordedEvents(2).isInstanceOf[BadgeCheckedOutAgain])
  }

  test("Should open a building") {
    // Given
    val building = new Building(BuildingId.fromString("A").get)

    // When
    building.open()

    // Then
    assert(building.getRecordedEvents.length == 1)
  }
}
