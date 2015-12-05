package net.pryden.accounts.commands;

import net.pryden.accounts.Marshaller;
import net.pryden.accounts.model.Config;

import javax.inject.Inject;

final class DumpConfigCommand implements Command {
  private final Marshaller marshaller;
  private final Config config;

  @Inject
  DumpConfigCommand(Marshaller marshaller, Config config) {
    this.marshaller = marshaller;
    this.config = config;
  }

  @Override
  public void run() {
    marshaller.dumpToConsole(config);
  }
}
