package org.lv.oversee.domain

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

final class InMemoryEventStore[T] extends EventStore[T] {
  private val eventStore: mutable.HashMap[StreamName, List[DomainEvent[T]]] = new mutable.HashMap[StreamName, List[DomainEvent[T]]]()

  override def commit(stream: Stream[T]): Try[Stream[T]] = {
    if (stream.isEmpty) Success(stream)
    else {
      val aggregateStream = eventStore.getOrElse(stream.streamName, List())

      try {
        eventStore.put(stream.streamName, aggregateStream ++ stream.streamEvents)

        Success(stream)
      } catch {
        case e: Throwable => Failure(e)
      }
    }
  }

  override def load(streamName: StreamName): Try[Option[Stream[T]]] = {
    Success(eventStore.get(streamName).map(Stream[T](streamName, _)))
  }
}
