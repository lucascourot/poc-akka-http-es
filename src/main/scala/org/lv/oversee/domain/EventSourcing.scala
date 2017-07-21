package org.lv.oversee.domain

import scala.collection.mutable.ListBuffer
import scala.util.Try

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

trait EventStore[T] {
  def commit(stream: Stream[T]): Try[Stream[T]]
  def load(streamName: StreamName): Try[Option[Stream[T]]]
}

case class Stream[T](streamName: StreamName, streamEvents: List[DomainEvent[T]]) {
  def isEmpty: Boolean = streamEvents.isEmpty
}

case class StreamName(name: String);
