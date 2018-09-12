package com.veamospues.cqrs.command;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Getter
public abstract class Command {
  private UUID aggregateId;

  private Long aggregateVersion;

  @Setter
  private String user;

  public Command(UUID aggregateId, Long aggregateVersion) {
    requireNonNull(aggregateId);
    requireNonNull(aggregateVersion);

    this.aggregateId = aggregateId;
    this.aggregateVersion = aggregateVersion;
  }
}
