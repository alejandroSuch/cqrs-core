package com.veamospues.cqrs.command.exception;

import com.veamospues.cqrs.command.Command;

public class UnknownCommandException extends RuntimeException {
  public UnknownCommandException(Command command) {
    super(String.format(
      "Unknown command %s for aggregate %s",
      command.getClass().getSimpleName(),
      command.getAggregateId()
    ));
  }
}
