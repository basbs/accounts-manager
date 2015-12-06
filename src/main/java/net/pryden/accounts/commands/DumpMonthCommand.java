package net.pryden.accounts.commands;

import net.pryden.accounts.Console;
import net.pryden.accounts.Marshaller;
import net.pryden.accounts.Storage;
import net.pryden.accounts.commands.Annotations.CurrentMonth;
import net.pryden.accounts.model.AccountsMonth;

import javax.inject.Inject;
import java.time.YearMonth;

final class DumpMonthCommand implements Command {
  private final Console console;
  private final Marshaller marshaller;
  private final Storage storage;
  private final YearMonth currentMonth;

  @Inject
  DumpMonthCommand(
      Console console,
      Marshaller marshaller,
      Storage storage,
      @CurrentMonth YearMonth currentMonth) {
    this.console = console;
    this.marshaller = marshaller;
    this.storage = storage;
    this.currentMonth = currentMonth;
  }

  @Override
  public void run() {
    AccountsMonth month = storage.readMonth(currentMonth);
    marshaller.dumpToConsole(month);
    console.print("\nTotals:\n");
    marshaller.dumpToConsole(month.computeTotals());
  }
}
