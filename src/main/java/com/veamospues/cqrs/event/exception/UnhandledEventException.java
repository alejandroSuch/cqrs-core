package com.veamospues.cqrs.event.exception;

import com.veamospues.cqrs.event.Event;

public class UnhandledEventException extends RuntimeException {
  public UnhandledEventException(Event event) {
    super(String.format(
      "Event %s (%s) for aggregate %s has not been handled",
      event.getId().toString(),
      event.getClass().getName(),
      event.getAggregateId().toString()
    ));
  }
}
