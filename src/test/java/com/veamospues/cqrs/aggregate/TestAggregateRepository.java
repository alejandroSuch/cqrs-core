package com.veamospues.cqrs.aggregate;

import com.veamospues.cqrs.event.EventBus;
import com.veamospues.cqrs.event.EventStore;

public class TestAggregateRepository extends AggregateRepository {
  public TestAggregateRepository(EventStore eventStore, EventBus eventBus) {
    super(eventStore, eventBus);
  }
}