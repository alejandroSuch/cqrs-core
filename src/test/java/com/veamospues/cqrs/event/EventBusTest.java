package com.veamospues.cqrs.event;

import com.veamospues.cqrs.event.exception.EventNotEmittedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;

import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventBusTest {
  private static final int TWO = 2;
  private static final VerificationMode CALLED_TWICE = times(TWO);

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Spy
  EventBus eventBus;

  @Mock
  Event event;

  @Mock
  Event anotherEvent;

  @Before
  public void setup() {
    when(event.getId()).thenReturn(UUID.randomUUID());
    when(event.getAggregateId()).thenReturn(UUID.randomUUID());

    when(anotherEvent.getId()).thenReturn(UUID.randomUUID());
    when(anotherEvent.getAggregateId()).thenReturn(UUID.randomUUID());
  }

  @Test
  public void emit_is_called_as_many_timesa_as_list_size() {
    // ARRANGE
    List<Event> eventList = asList(event, anotherEvent);
    doNothing().when(eventBus).emit(any(Event.class));

    // ACT
    eventBus.emit(eventList);

    // ASSERT
    verify(eventBus).emit(event);
    verify(eventBus).emit(anotherEvent);

    verify(eventBus, times(eventList.size())).emit(any(Event.class));
  }

  @Test
  public void first_emit_is_called_when_second_fails() {
    // ARRANGE
    List<Event> eventList = asList(event, anotherEvent);
    doNothing().when(eventBus).emit(event);
    doThrow(new EventNotEmittedException(singletonList(anotherEvent))).when(eventBus).emit(anotherEvent);

    // ACT
    thrown.expect(EventNotEmittedException.class);
    eventBus.emit(eventList);

    // ASSERT
    verify(eventBus).emit(event);
  }

  @Test
  public void second_emit_is_called_when_first_fails() {
    // ARRANGE
    List<Event> eventList = asList(event, anotherEvent);
    doThrow(new EventNotEmittedException(singletonList(event))).when(eventBus).emit(event);
    doNothing().when(eventBus).emit(anotherEvent);

    // ACT
    thrown.expect(EventNotEmittedException.class);
    eventBus.emit(eventList);

    // ASSERT
    verify(eventBus).emit(anotherEvent);
  }

}