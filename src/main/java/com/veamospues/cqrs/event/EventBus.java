package com.veamospues.cqrs.event;

import com.veamospues.cqrs.event.exception.EventNotEmittedException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class EventBus<E extends Event> {

  public abstract void emit(E eventToEmit) throws EventNotEmittedException;

  public abstract void on(Class<E> event, Function<E, Void> callback);

  public void emit(Iterable<E> events) {
    List<E> unemittedEvents = new ArrayList<>();

    events.forEach(event -> {
      try {
        this.emit(event);
      } catch (EventNotEmittedException e) {
        unemittedEvents.add(event);
      }
    });

    throwExceptionIf(unemittedEvents);
  }

  void throwExceptionIf(List<E> unemittedEvents) {
    if (unemittedEvents.size() > 0) {
      throw new EventNotEmittedException(unemittedEvents);
    }
  }
}
