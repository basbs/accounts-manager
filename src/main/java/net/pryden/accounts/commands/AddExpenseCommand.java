package net.pryden.accounts.commands;

import net.pryden.accounts.Console;
import net.pryden.accounts.Storage;
import net.pryden.accounts.commands.Annotations.CurrentMonth;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.Money;
import net.pryden.accounts.model.Transaction;
import net.pryden.accounts.model.TransactionCategory;

import javax.inject.Inject;
import java.time.YearMonth;

final class AddExpenseCommand implements Command {
  private final Console console;
  private final Storage storage;
  private final YearMonth currentMonth;

  @Inject
  AddExpenseCommand(Console console, Storage storage, @CurrentMonth YearMonth currentMonth) {
    this.console = console;
    this.storage = storage;
    this.currentMonth = currentMonth;
  }

  @Override
  public void run() throws Exception {
    int date = console.readInt("Date (day of the month): ");
    String description = console.readString("Transaction description: ");
    String summaryDescription =
        console.readString(
            String.format("Transaction summary (for accounts report) [%s]: ", description));
    Money amount = console.readMoney("Amount: ");

    AccountsMonth month = storage.readMonth(currentMonth);
    AccountsMonth updated = month.withNewTransactions(Transaction.builder()
        .setDate(date)
        .setDescription(description)
        .setSummaryDescription(summaryDescription)
        .setCategory(TransactionCategory.EXPENSE)
        .setCheckingOut(amount)
        .build());
    if (console.readConfirmation("Adding new transaction to %s", currentMonth)) {
      storage.writeMonth(updated);
    }
  }
}
