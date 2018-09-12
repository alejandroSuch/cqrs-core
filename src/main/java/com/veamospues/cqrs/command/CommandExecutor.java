package com.veamospues.cqrs.command;

public interface CommandExecutor<T extends Command> {
  void execute(T command);
  // TODO: void execute(T command, String user);
}
