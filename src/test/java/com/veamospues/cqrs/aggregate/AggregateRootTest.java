package com.veamospues.cqrs.aggregate;

import com.veamospues.cqrs.event.Event;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static java.util.stream.StreamSupport.stream;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AggregateRootTest {

  private static final int ZERO_RESULTS = 0;
  private static final int ONE_RESULT = 1;
  private static final Long VERSION_ZERO = 0L;
  private static final Long UNINITIALIZED_VERSION = -1L;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Spy
  AggregateRoot aggregateRoot;

  @Mock
  Event anEvent;

  @Before
  public void setup() {
    doNothing().when(aggregateRoot).handle(any(Event.class));

    when(anEvent.getVersion()).thenReturn(VERSION_ZERO);
  }

  @Test
  public void uncommited_changes_are_void_when_nothing_is_done() {
    // ARRANGE

    // ACT
    final Iterable uncommitedChanges = aggregateRoot.getUncommitedChanges();

    // ASSERT
    assertEquals(UNINITIALIZED_VERSION, aggregateRoot.getVersion());
    assertEquals(ZERO_RESULTS, stream(uncommitedChanges.spliterator(), false).count());
  }

  @Test
  public void uncommited_changes_are_one_when_an_event_is_added() {
    // ARRANGE

    // ACT
    aggregateRoot.applyChange(anEvent);
    final Iterable uncommitedChanges = aggregateRoot.getUncommitedChanges();

    // ASSERT
    assertEquals(UNINITIALIZED_VERSION, aggregateRoot.getVersion());
    assertEquals(ONE_RESULT, stream(uncommitedChanges.spliterator(), false).count());
    assertTrue(aggregateRoot.hasUncommitedChanges());
  }

  @Test
  public void uncommited_changes_are_zero_when_an_event_is_added_and_then_changes_are_marked_as_commited() {
    // ARRANGE

    // ACT
    aggregateRoot.applyChange(anEvent);
    aggregateRoot.markChangesAsCommited();
    final Iterable uncommitedChanges = aggregateRoot.getUncommitedChanges();

    // ASSERT
    assertEquals(UNINITIALIZED_VERSION, aggregateRoot.getVersion());
    assertEquals(ZERO_RESULTS, stream(uncommitedChanges.spliterator(), false).count());
    assertFalse(aggregateRoot.hasUncommitedChanges());
  }

  @Test
  public void when_loading_from_history_there_are_no_uncommited_changes_and_version_is_set() {
    // ARRANGE
    final List<Event> history = singletonList(anEvent);

    // ACT
    aggregateRoot.loadFromHistory(history);

    // ASSERT
    assertEquals(VERSION_ZERO, aggregateRoot.getVersion());
    assertFalse(aggregateRoot.hasUncommitedChanges());
  }

  @Test
  public void fails_when_instantiating_an_aggregate_root_with_null_uuid() {
    // ARRANGE
    final UUID uuid = null;
    thrown.expect(NullPointerException.class);

    // ACT
    new TestAggregateRoot(uuid);

    // ASSERT
  }

  @Test
  public void ok_when_instantiating_an_aggregate_root_with_null_uuid() {
    // ARRANGE
    final UUID uuid = UUID.randomUUID();

    // ACT
    final TestAggregateRoot aggregateRoot = new TestAggregateRoot(uuid);

    // ASSERT
    assertEquals(uuid, aggregateRoot.getId());
  }

  @Test
  public void fails_when_setting_a_null_version() {
    // ARRANGE
    final Long version = null;
    thrown.expect(NullPointerException.class);

    // ACT
    aggregateRoot.setVersion(version);

    // ASSERT
  }

  @Test
  public void ok_when_setting_a_non_null_version() {
    // ARRANGE
    final Long version = 1L;

    // ACT
    aggregateRoot.setVersion(version);

    // ASSERT
    assertEquals((Long) 1L, aggregateRoot.getVersion());
  }
}