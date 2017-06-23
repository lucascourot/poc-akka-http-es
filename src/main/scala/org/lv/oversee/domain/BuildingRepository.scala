package org.lv.oversee.domain

import scala.util.{Failure, Try}

trait BuildingRepository {
  def get(buildingId: BuildingId): Option[Building]

  def save(building: Building): Try[Building]
}

object EntityNotFoundRepository {
  def withNotFound[A, B](exec: A => Option[B])(id: A): Try[B] =
    exec(id) match {
      case Some(v) => Try(v)
      case _ => Failure(new Error("..."))
    }
}
