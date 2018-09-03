package com.veamospues.cqrs.event;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class EventTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void empty_event_brings_no_data() {
    // ARRANGE

    // ACT
    TestEvent event = new TestEvent();

    // ASSERT
    assertNull(event.getId());
    assertNull(event.getAggregateId());
    assertNull(event.getDate());
  }

  @Test
  public void non_empty_event_brings_some_data() {
    // ARRANGE
    UUID aggregateId = randomUUID();

    // ACT
    TestEvent event = new TestEvent(aggregateId);

    // ASSERT
    assertNotNull(event.getId());
    assertNotNull(event.getAggregateId());
    assertNotNull(event.getDate());
  }

  @Test
  public void exception_on_null_id() {
    // ARRANGE
    UUID aggregateId = null;

    // ASSERT
    thrown.expect(NullPointerException.class);

    // ACT
    new TestEvent(aggregateId);
  }

  private class TestEvent extends Event {
    public TestEvent() {
    }

    public TestEvent(UUID aggregateId) {
      super(aggregateId);
    }
  }
}