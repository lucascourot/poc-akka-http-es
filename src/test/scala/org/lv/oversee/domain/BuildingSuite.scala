package org.lv.oversee.domain

import org.scalatest.FunSuite

import scala.collection.convert.WrapAsJava.`deprecated seqAsJavaList`

class BuildingSuite extends FunSuite{
  test("Building should check in badge id") {
    // Given
    val building = new Building(BuildingId.fromString("A").get)
    val badgeId = BadgeId.fromString("B").get

    // When
    building.checkIn(badgeId)

    // Then
    assert(building.getRecordedEvents.length == 1)
    assert(building.getRecordedEvents.get(0).isInstanceOf[BadgeCheckedIn])
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
    assert(building.getRecordedEvents.get(0).isInstanceOf[BadgeCheckedIn])
    assert(building.getRecordedEvents.get(1).isInstanceOf[AccessRefused])
  }
}
