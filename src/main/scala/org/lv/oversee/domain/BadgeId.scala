package org.lv.oversee.domain

import scala.util.{Failure, Success, Try}

final case class BadgeId(id: String) {
  override def toString: String = id
}

object BadgeId {
  def fromString(id: String): Try[BadgeId] = {
    if (id.length() > 50) Failure(InvalidBadgeId(s"$id is invalid"))
    else Success(BadgeId(id))
  }
}

case class InvalidBadgeId(message: String) extends Exception(message)
