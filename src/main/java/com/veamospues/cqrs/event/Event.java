package com.veamospues.cqrs.event;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;
import static org.joda.time.DateTime.now;

@Data
public abstract class Event {
  private UUID id;
  private UUID aggregateId;
  private Long version;
  private Date date;
  private String user;

  public Event() {
  }

  public Event(UUID aggregateId) {
    requireNonNull(aggregateId);

    this.id = randomUUID();
    this.aggregateId = aggregateId;
    this.date = now().toDate();
  }
}
