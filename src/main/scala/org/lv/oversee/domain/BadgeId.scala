package org.lv.oversee.domain

import scala.util.{Failure, Success, Try}

final case class BadgeId(id: String) {
  override def toString = id
}

object BadgeId {
  def fromString(id: String): Try[BadgeId] = {
    if (id.length() != 5) Failure(InvalidBadgeId(s"$id is invalid"))
    else Success(BadgeId(id))
  }
}
