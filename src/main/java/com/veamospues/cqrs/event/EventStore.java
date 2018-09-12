package com.veamospues.cqrs.event;

import java.util.List;
import java.util.UUID;

public interface EventStore<E extends Event> {
  List<E> getEventsFor(UUID aggregateID);

  List<E> getAllEvents();

  void save(E event);
}
