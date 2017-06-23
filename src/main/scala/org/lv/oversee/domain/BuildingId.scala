package org.lv.oversee.domain

import scala.util.{Failure, Success, Try}

final case class BuildingId(id: String) {
  override def toString: String = id
}

object BuildingId {
  def fromString(id: String): Try[BuildingId] = {
    if (id.length() > 50) Failure(InvalidBuildingId(s"$id is invalid"))
    else Success(BuildingId(id))
  }
}

case class InvalidBuildingId(message: String) extends Exception(message)
