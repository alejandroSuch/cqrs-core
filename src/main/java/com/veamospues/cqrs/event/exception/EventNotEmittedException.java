package com.veamospues.cqrs.event.exception;

import com.veamospues.cqrs.event.Event;

import java.util.List;
import java.util.stream.Collectors;

public class EventNotEmittedException extends RuntimeException {
  public <E extends Event> EventNotEmittedException(E event) {
    super(String.format("Event %s (%s) has not been emitted", event.getId().toString(), event.getClass().getName()));
  }

  public <E extends Event> EventNotEmittedException(List<E> events) {
    super(
      "Following events have not been emitted:\n" +
        events
          .stream()
          .map(it -> String.format(" - %s (%s). Aggregate id: %s.\n", it.getId().toString(), it.getClass().getName(), it.getAggregateId().toString()))
          .collect(Collectors.joining(""))
    );
  }
}
