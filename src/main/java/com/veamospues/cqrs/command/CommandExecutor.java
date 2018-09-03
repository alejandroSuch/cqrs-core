package com.veamospues.cqrs.command;

public interface CommandExecutor<C extends Command> {
  void execute(C command);
}
