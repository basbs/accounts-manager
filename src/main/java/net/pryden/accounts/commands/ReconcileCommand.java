package net.pryden.accounts.commands;

import com.google.common.collect.ComparisonChain;
import net.pryden.accounts.Console;
import net.pryden.accounts.Storage;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.ComputedTotals;
import net.pryden.accounts.model.Money;
import net.pryden.accounts.model.Reconciliation;
import net.pryden.accounts.model.UnreconciledTransaction;

import javax.inject.Inject;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class ReconcileCommand implements Command {
  private final Console console;
  private final Storage storage;
  private final Clock clock;

  @Inject
  ReconcileCommand(Console console, Storage storage, Clock clock) {
    this.console = console;
    this.storage = storage;
    this.clock = clock;
  }

  @Override
  public void run() throws Exception {
    LocalDate closingDate = console.readDate("Statement closing date [YYYY-MM-DD]: ");
    Money closingBalance = console.readMoney("Closing balance: ");
    YearMonth yearMonth = YearMonth.of(closingDate.getYear(), closingDate.getMonth());
    AccountsMonth previousMonth = storage.readMonth(yearMonth.minusMonths(1));
    AccountsMonth month = storage.readMonth(yearMonth);
    if (!(previousMonth.isClosed() && previousMonth.reconciliation().isPresent())) {
      throw new IllegalStateException(
          String.format(
              "Trying to reconcile %s but the previous month (%s) has not yet been reconciled.",
              yearMonth,
              previousMonth.date()));
    }
    if (!month.isClosed()) {
      throw new IllegalStateException(
          String.format(
              "Trying to reconcile %s but the month has not been closed yet. "
                  + "You will need to first close the month using the close-month command.",
              yearMonth));
    }
    List<UnreconciledTransaction> toBeReconciled =
        Stream.concat(
            // the previous (closed and reconciled) month's unreconciled transactions,
            previousMonth
                .reconciliation()
                .get()
                .unreconciledTransactions()
                .stream(),
            // plus all this month's transactions...
            month
                .transactions()
                .stream()
                // ... only including transactions that affect the checking balance ...
                .filter(t -> !t.isZeroChecking())
                // ... converted to UnreconciledTransaction objects.
                .map(t -> UnreconciledTransaction.builder()
                    .setDate(month.date().atDay(t.date()))
                    .setDescription(t.description())
                    .setAmount(t.checkingIn().minus(t.checkingOut()))
                    .build()))
        // Sort all the unreconciled transactions:
        .sorted((a, b) -> ComparisonChain.start()
            // Sort all deposits before all expenses
            .compareTrueFirst(a.amount().isPositive(), b.amount().isPositive())
            // Then sort by date
            .compare(a.date(), b.date())
            .result())
        .collect(Collectors.toList());

    // We start the reconciliation with the closing balance from the bank statement.
    Money reconciledBalance = closingBalance;
    List<UnreconciledTransaction> unreconciled = new ArrayList<>();
    for (UnreconciledTransaction transaction : toBeReconciled) {
      if (transaction.date().isAfter(closingDate)) {
        continue;
      }
      console.printf("%s: %s - %s\n",
          transaction.date().format(DateTimeFormatter.ISO_DATE),
          transaction.amount(),
          transaction.description());
      if (!console.readConfirmation("Does the statement contain this transaction?")) {
        reconciledBalance = reconciledBalance.plus(transaction.amount());
        unreconciled.add(transaction);
      }
    }
    ComputedTotals totals = month.computeTotals();
    if (reconciledBalance.equals(totals.checkingBalance())) {
      console.printf("Congratulations, the closing balance of %s matches\n", closingBalance);
    } else {
      console.printf(
          "The checking balance at month end was %s\n"
              + "But the reconciled balance is %s\n"
              + "There is a discrepancy of %s -- please investigate and correct this discrepancy\n",
          totals.checkingBalance(),
          reconciledBalance,
          totals.checkingBalance().minus(reconciledBalance));
      return;
    }
    AccountsMonth updatedMonth = month.toBuilder()
        .setReconciliation(Reconciliation.builder()
            .setDateReconciled(LocalDate.now(clock))
            .setStatementBalance(closingBalance)
            .setReconciledBalance(reconciledBalance)
            .setUnreconciledTransactions(unreconciled)
            .build())
        .build();
    if (console.readConfirmation("Write updates to %s?", yearMonth)) {
      storage.writeMonth(updatedMonth);
    }
    // TODO(dpryden): Generate reconciliation report
  }
}
