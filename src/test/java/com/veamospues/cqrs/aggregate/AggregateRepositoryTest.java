package com.veamospues.cqrs.aggregate;

import com.veamospues.cqrs.aggregate.exception.AggregatePreviuoslyModifiedException;
import com.veamospues.cqrs.event.Event;
import com.veamospues.cqrs.event.EventBus;
import com.veamospues.cqrs.event.EventStore;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;

import java.util.UUID;

import static java.lang.Boolean.FALSE;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AggregateRepositoryTest {
  private static final VerificationMode CALLED_ONCE = times(1);
  private static final long VERSION_1 = 1L;

  @Mock
  EventStore eventStore;

  @Mock
  EventBus eventBus;

  @Mock
  Event event;

  AggregateRepository aggregateRepository;

  @Spy
  AggregateRoot aggregate;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void setup() {
    aggregateRepository = spy(new TestAggregateRepository(eventStore, eventBus));
  }

  @Test
  public void nothing_happens_when_aggregate_has_not_uncommited_changes() {
    // ARRANGE
    when(aggregate.hasUncommitedChanges()).thenReturn(FALSE);

    // ACT
    aggregateRepository.save(aggregate, VERSION_1);

    // ASSERT
    verify(aggregateRepository, CALLED_ONCE).save(aggregate, VERSION_1);
    verifyNoMoreInteractions(aggregateRepository);
  }

  @Test
  public void error_when_versions_do_not_match() {
    // ARRANGE
    final Long expectedVersion = 0L;
    aggregate.applyChange(event);
    when(aggregate.getId()).thenReturn(randomUUID());

    //ASSERT
    thrown.expect(AggregatePreviuoslyModifiedException.class);

    // ACT
    aggregateRepository.save(aggregate, expectedVersion);
  }

  @Test
  public void save_ok() {
    // ARRANGE
    final Long expectedVersion = 0L;
    final Long newVersion = 1L;
    final UUID aggregateId = randomUUID();

    when(event.getVersion()).thenReturn(0L);
    when(eventStore.getEventsFor(aggregateId)).thenReturn(asList(event));

    // ACT
    AggregateRoot byId = aggregateRepository.getById(aggregate.getClass(), aggregateId);
    byId.applyChange(event);
    aggregateRepository.save(byId, expectedVersion);

    // ASSERT
    verify(event).setVersion(1L);
    verify(eventStore).save(event);
    assertEquals(newVersion, byId.getVersion());
  }

  @Test
  public void get_by_id_fails_on_null_uuid() {
    // ARRANGE
    final UUID id = null;
    thrown.expect(NullPointerException.class);

    // ACT
    aggregateRepository.getById(aggregate.getClass(), id);

    // ASSERT
  }

  @Test
  public void get_by_id_fails_on_null_aggregate() {
    // ARRANGE
    final UUID id = randomUUID();
    final Class nullAggregateClass = null;
    thrown.expect(NullPointerException.class);

    // ACT
    aggregateRepository.getById(nullAggregateClass, id);

    // ASSERT
  }

  @Test
  public void get_by_id_returns_an_uninitialized_aggregate_when_store_returns_empty_list() {
    // ARRANGE
    final UUID id = randomUUID();
    when(eventStore.getEventsFor(id)).thenReturn(emptyList());

    // ACT
    AggregateRoot byId = aggregateRepository.getById(aggregate.getClass(), id);

    // ASSERT
    assertEquals(id, byId.getId());
    assertEquals(new Long(-1), byId.getVersion());
  }

  @Test
  public void get_by_id_returns_an_initialized_aggregate_when_store_returns_non_empty_list() {
    // ARRANGE
    final UUID id = randomUUID();
    when(eventStore.getEventsFor(id)).thenReturn(asList(event));
    when(event.getVersion()).thenReturn(1L);

    // ACT
    AggregateRoot byId = aggregateRepository.getById(aggregate.getClass(), id);

    // ASSERT
    assertEquals(id, byId.getId());
    assertEquals(event.getVersion(), byId.getVersion());
  }
}