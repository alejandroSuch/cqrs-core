package com.veamospues.cqrs.aggregate;

import com.veamospues.cqrs.event.Event;

import java.util.UUID;

public class TestAggregateRoot extends AggregateRoot {
  public TestAggregateRoot() {
  }

  public TestAggregateRoot(UUID id) {
    super(id);
  }

  @Override
  protected void handle(Event event) {

  }
}