package net.pryden.accounts.commands;

/**
 * A particular command that can be invoked.
 */
public interface Command {
  void run() throws Exception;
}
