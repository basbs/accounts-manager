package net.pryden.accounts.commands;

import com.google.common.collect.ImmutableSet;
import net.pryden.accounts.Storage;
import net.pryden.accounts.commands.Annotations.CurrentMonth;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.reports.Report;

import javax.inject.Inject;
import java.io.IOException;
import java.time.YearMonth;

final class GenerateFormsCommand implements Command {
  private final Storage storage;
  private final YearMonth currentMonth;
  private final ImmutableSet<Report> reports;

  @Inject
  GenerateFormsCommand(
      Storage storage,
      @CurrentMonth YearMonth currentMonth,
      ImmutableSet<Report> reports) {
    this.storage = storage;
    this.currentMonth = currentMonth;
    this.reports = reports;
  }

  @Override
  public void run() throws IOException {
    AccountsMonth month = storage.readMonth(currentMonth);
    for (Report report : reports) {
      if (report.isApplicableFor(month)) {
        report.generate(month);
      }
    }
  }
}
