package com.veamospues.cqrs.aggregate.exception;

public class AggregateCreationException extends RuntimeException {
  public AggregateCreationException(Exception e) {
    super(e);
  }
}
