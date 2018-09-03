package com.veamospues.cqrs.command;

import lombok.Getter;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

public abstract class Command {
  @Getter
  private UUID aggregateId;

  @Getter
  private Long aggregateVersion;

  public Command(UUID aggregateId, Long aggregateVersion) {
    requireNonNull(aggregateId);
    requireNonNull(aggregateVersion);

    this.aggregateId = aggregateId;
    this.aggregateVersion = aggregateVersion;
  }
}
