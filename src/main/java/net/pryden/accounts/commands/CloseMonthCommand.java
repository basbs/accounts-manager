package net.pryden.accounts.commands;

import com.google.common.collect.ImmutableList;
import net.pryden.accounts.Console;
import net.pryden.accounts.Storage;
import net.pryden.accounts.commands.Annotations.CurrentMonth;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.BranchResolution;
import net.pryden.accounts.model.BranchResolutionType;
import net.pryden.accounts.model.ComputedTotals;
import net.pryden.accounts.model.Config;
import net.pryden.accounts.model.SubTransaction;
import net.pryden.accounts.model.Transaction;
import net.pryden.accounts.model.TransactionCategory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

final class CloseMonthCommand implements Command {
  private final Console console;
  private final Config config;
  private final Storage storage;
  private final YearMonth currentMonth;
  private final Provider<AddDepositCommand> addDepositCommandProvider;
  private final Provider<GenerateFormsCommand> generateFormsCommandProvider;

  @Inject
  CloseMonthCommand(
      Console console,
      Config config,
      Storage storage,
      @CurrentMonth YearMonth currentMonth,
      Provider<AddDepositCommand> addDepositCommandProvider,
      Provider<GenerateFormsCommand> generateFormsCommandProvider) {
    this.console = console;
    this.config = config;
    this.storage = storage;
    this.currentMonth = currentMonth;
    this.addDepositCommandProvider = addDepositCommandProvider;
    this.generateFormsCommandProvider = generateFormsCommandProvider;
  }

  @Override
  public void run() throws Exception {
    AccountsMonth month = storage.readMonth(currentMonth);
    ComputedTotals totals = month.computeTotals();
    if (!totals.receiptsOutstandingBalance().equals(BigDecimal.ZERO)) {
      if (console.readConfirmation("There is a receipts balance of %s remaining. "
          + "Would you like to add a deposit?", totals.receiptsOutstandingBalance())) {
        addDepositCommandProvider.get().run();
        month = storage.readMonth(currentMonth);
        totals = month.computeTotals();
      }
    }

    int date = month.date().atEndOfMonth().getDayOfMonth();

    List<SubTransaction> transferTransactions = new ArrayList<>();
    transferTransactions.add(SubTransaction.builder()
        .setDescription("Worldwide Work")
        .setCategory(TransactionCategory.WORLDWIDE_WORK)
        .setType(BranchResolutionType.WORLDWIDE_WORK_FROM_CONTRIBUTION_BOXES)
        .setAmount(totals.totalWorldwideReceipts())
        .build());
    BigDecimal totalTransfer = totals.totalWorldwideReceipts();
    for (BranchResolution resolution : config.branchResolutions()) {
      transferTransactions.add(SubTransaction.builder()
          .setDescription(resolution.description())
          .setCategory(resolution.category())
          .setType(resolution.type())
          .setAmount(resolution.amount())
          .build());
      totalTransfer = totalTransfer.add(resolution.amount());
    }

    // TODO(dpryden): Add support for storing the transfer confirmation number

    month = month.withNewTransactions(Transaction.builder()
        .setDate(date)
        .setDescription("jw.org Transfer")
        .setCategory(TransactionCategory.OTHER)
        .setCheckingOut(totalTransfer)
        .setSubTransactions(ImmutableList.copyOf(transferTransactions))
        .build());
    month = month.toBuilder().setIsClosed(true).build();
    Config updatedConfig = config.toBuilder().setCurrentMonth(currentMonth.plusMonths(1)).build();
    if (console.readConfirmation("Confirm closing month %s", currentMonth)) {
      storage.writeMonth(month);
      storage.updateConfig(updatedConfig);
    }
    if (console.readConfirmation("Generate PDFs for %s?", currentMonth)) {
      generateFormsCommandProvider.get().run();
    }
  }
}
