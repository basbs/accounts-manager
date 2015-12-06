package net.pryden.accounts.commands;

import net.pryden.accounts.Console;
import net.pryden.accounts.Storage;
import net.pryden.accounts.commands.Annotations.CurrentMonth;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.ComputedTotals;
import net.pryden.accounts.model.Transaction;
import net.pryden.accounts.model.TransactionCategory;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.YearMonth;

final class AddDepositCommand implements Command {
  private final Console console;
  private final Storage storage;
  private final YearMonth currentMonth;

  @Inject
  AddDepositCommand(Console console, Storage storage, @CurrentMonth YearMonth currentMonth) {
    this.console = console;
    this.storage = storage;
    this.currentMonth = currentMonth;
  }

  @Override
  public void run() throws Exception {
    AccountsMonth month = storage.readMonth(currentMonth);
    ComputedTotals totals = month.computeTotals();
    if (totals.receiptsOutstandingBalance().signum() == 0) {
      if (!console.readConfirmation("There is currently no receipts balance for %s. "
          + "Are you sure you want to make a deposit?", currentMonth)) {
        return;
      }
    }
    int date = console.readInt("Date (day of the month): ");
    String description = console.readString("Description [Deposit to checking account]: ");
    if (description.isEmpty()) {
      description = "Deposit to checking account";
    }
    BigDecimal amount = console.readMoney(
        String.format("Amount to deposit [%s]: ", totals.receiptsOutstandingBalance()),
        totals.receiptsOutstandingBalance());

    AccountsMonth updated = month.withNewTransactions(Transaction.builder()
        .setDate(date)
        .setDescription(description)
        .setCategory(TransactionCategory.DEPOSIT)
        .setReceiptsOut(amount)
        .setCheckingIn(amount)
        .build());
    if (console.readConfirmation("Adding new transaction to %s", currentMonth)) {
      storage.writeMonth(updated);
    }
  }
}
