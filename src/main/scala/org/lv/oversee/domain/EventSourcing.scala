package org.lv.oversee.domain

import scala.collection.mutable.ListBuffer

trait DomainEvent[T] {
  type getAggregateId<:T
}

trait Aggregate[Type, TypeId] {
  lazy val events: ListBuffer[DomainEvent[TypeId]] = ListBuffer()

  def reconstituteFromHistory(events: List[DomainEvent[TypeId]]): Type

  def apply(domainEvent: DomainEvent[TypeId]): DomainEvent[TypeId]

  def recordThat(domainEvent: DomainEvent[TypeId]): DomainEvent[TypeId] = {
    events.append(domainEvent)

    apply(domainEvent)
  }

  def getRecordedEvents: List[DomainEvent[TypeId]] = events.toList
}
