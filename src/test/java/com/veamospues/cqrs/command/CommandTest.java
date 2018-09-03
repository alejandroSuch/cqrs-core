package com.veamospues.cqrs.command;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class CommandTest {
  private static final UUID AGGREGATE_ID = UUID.randomUUID();
  private static final long AGGREGATE_VERSION = 1L;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void it_fails_on_null_aggregate_id() {
    // ARRANGE
    Long aggregateVersion = AGGREGATE_VERSION;
    UUID aggregateId = null;

    // ASSERT
    thrown.expect(NullPointerException.class);

    // ACT
    new TestCommand(aggregateId, aggregateVersion);
  }

  @Test
  public void it_fails_on_null_aggregate_version() {
    // ARRANGE
    Long aggregateVersion = null;
    UUID aggregateId = AGGREGATE_ID;

    // ASSERT
    thrown.expect(NullPointerException.class);

    // ACT
    new TestCommand(aggregateId, aggregateVersion);
  }

  @Test
  public void ok_when_aggregate_version_and_aggregate_id_are_not_null() {
    // ARRANGE
    Long aggregateVersion = AGGREGATE_VERSION;
    UUID aggregateId = AGGREGATE_ID;

    // ACT
    TestCommand testCommand = new TestCommand(aggregateId, aggregateVersion);

    // ASSERT
    assertEquals(testCommand.getAggregateId(), AGGREGATE_ID);
    assertEquals((Long) testCommand.getAggregateVersion(), (Long) AGGREGATE_VERSION);
  }

  private class TestCommand extends Command {
    public TestCommand(UUID aggregateId, Long aggregateVersion) {
      super(aggregateId, aggregateVersion);
    }
  }

}