package com.veamospues.cqrs.aggregate;

import com.veamospues.cqrs.aggregate.exception.AggregateCreationException;
import com.veamospues.cqrs.aggregate.exception.AggregatePreviuoslyModifiedException;
import com.veamospues.cqrs.event.Event;
import com.veamospues.cqrs.event.EventBus;
import com.veamospues.cqrs.event.EventStore;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

@Log
@Getter
public abstract class AggregateRepository<T extends AggregateRoot<E>, E extends Event> {
  private static final int SNAPSHOT_THRESHOLD = 10; // TODO: BY CONFIGURATION

  private EventStore<E> eventStore;
  private EventBus<E> eventBus;

  public AggregateRepository(EventStore<E> eventStore, EventBus<E> eventBus) {
    requireNonNull(eventStore);
    requireNonNull(eventBus);

    this.eventStore = eventStore;
    this.eventBus = eventBus;
  }

  public void save(T aggregate, Long expectedVersion) {
    if (!aggregate.hasUncommitedChanges()) {
      return;
    }

    assertThatVersionsMatch(aggregate, aggregate.getId(), expectedVersion);
    saveUncommitedChanges(aggregate);
    eventBus.emit(aggregate.getUncommitedChanges());
    aggregate.markChangesAsCommited();
  }

  public void save(T aggregate, Long expectedVersion, String user) {
    aggregate.getUncommitedChanges().forEach(change -> change.setUser(user));
    save(aggregate, expectedVersion);
  }

  private void assertThatVersionsMatch(T aggregate, UUID id, Long expectedVersion) {
    final T item = getById((Class<T>) aggregate.getClass(), id);

    if (expectedVersion != -1L && !item.getVersion().equals(expectedVersion)) {
      throw new AggregatePreviuoslyModifiedException(item.getId());
    }
  }

  public T getById(Class<T> clazz, UUID id) {
    requireNonNull(clazz);
    requireNonNull(id);

    final T item = newAggregateRoot(clazz);
    List<E> events = eventStore.getEventsFor(id);
    /* TODO: Return null if no history?
    if(events.size() == 0) {
      return null;
    } */

    item.loadFromHistory(events);
    item.setId(id);
    return item;
  }

  private T newAggregateRoot(Class<T> clazz) {
    try {
      return clazz.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new AggregateCreationException(e);
    }
  }

  private void saveUncommitedChanges(T aggregate) {
    final Version version = new Version(aggregate.getVersion());

    aggregate.getUncommitedChanges().forEach(event -> {
      version.increment();

      createSnapshotIfNeeded(aggregate, version.value());
      event.setVersion(version.value());
      eventStore.save(event);
    });

    aggregate.setVersion(version.value());
  }

  private void createSnapshotIfNeeded(T aggregate, Long version) {
    if (version > SNAPSHOT_THRESHOLD && version % SNAPSHOT_THRESHOLD == 0) {
      // TODO: Create snapshot
      log.info(format("We should create a snapshopt for aggregate $s", aggregate.getId().toString()));
    }
  }

  private class Version {
    private Long version;

    Version(Long version) {
      requireNonNull(version);

      this.version = version;
    }

    void increment() {
      version++;
    }

    Long value() {
      return version;
    }
  }
}
