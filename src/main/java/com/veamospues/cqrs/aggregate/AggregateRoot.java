package com.veamospues.cqrs.aggregate;

import com.veamospues.cqrs.event.Event;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static java.util.stream.StreamSupport.stream;


public abstract class AggregateRoot<E extends Event> {

  private final Set<E> changes = new LinkedHashSet<>();

  private UUID id;
  private Long version = -1L;

  public AggregateRoot() {
  }

  public AggregateRoot(UUID id) {
    requireNonNull(id);

    this.id = id;
  }

  abstract void handle(E event);

  public Iterable<E> getUncommitedChanges() {
    return changes;
  }

  public Boolean hasUncommitedChanges() {
    return stream(changes.spliterator(), false).count() > 0;
  }

  public void markChangesAsCommited() {
    changes.clear();
  }

  public void loadFromHistory(Iterable<E> history) {
    history.forEach(event -> {
      applyChange(event, Boolean.FALSE);
      version = event.getVersion();
    });

  }

  protected void applyChange(E event) {
    applyChange(event, Boolean.TRUE);
  }

  void applyChange(E event, Boolean isNew) {
    handle(event);

    if (isNew) {
      changes.add(event);
    }
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    requireNonNull(id);

    this.id = id;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    requireNonNull(version);

    this.version = version;
  }
}
