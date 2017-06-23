package org.lv.oversee.domain

trait DomainEvent[T] {
  def getAggregateId: T
}
