package com.veamospues.cqrs.aggregate.exception;

import java.util.UUID;

public class AggregatePreviuoslyModifiedException extends RuntimeException {
  public AggregatePreviuoslyModifiedException(UUID id) {
    super(String.format("Aggregate %s has been previously modified", id.toString()));
  }
}
