package net.pryden.accounts.commands;

import net.pryden.accounts.Console;
import net.pryden.accounts.Storage;
import net.pryden.accounts.commands.Annotations.CurrentMonth;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.Transaction;
import net.pryden.accounts.model.TransactionCategory;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

final class AddReceiptsCommand implements Command {
  private final YearMonth currentMonth;
  private final Storage storage;
  private final Console console;

  @Inject
  AddReceiptsCommand(@CurrentMonth YearMonth currentMonth, Storage storage, Console console) {
    this.currentMonth = currentMonth;
    this.storage = storage;
    this.console = console;
  }

  @Override
  public void run() throws Exception {
    int date = console.readInt("Date (day of the month): ");
    BigDecimal cong = console.readMoney("Local Congregation Expenses: ", BigDecimal.ZERO);
    BigDecimal worldwide = console.readMoney("Worldwide Work: ", BigDecimal.ZERO);

    List<Transaction> transactions = new ArrayList<>();
    if (!cong.equals(BigDecimal.ZERO)) {
      transactions.add(Transaction.builder()
          .setDate(date)
          .setDescription("Contributions - Local Congregation Expenses")
          .setCategory(TransactionCategory.LOCAL_CONGREGATION_EXPENSES)
          .setReceiptsIn(cong)
          .build());
    }
    if (!worldwide.equals(BigDecimal.ZERO)) {
      transactions.add(Transaction.builder()
          .setDate(date)
          .setDescription("Contributions - Worldwide Work")
          .setCategory(TransactionCategory.WORLDWIDE_WORK)
          .setReceiptsIn(worldwide)
          .build());
    }

    AccountsMonth month = storage.readMonth(currentMonth);
    AccountsMonth updated = month.withNewTransactions(transactions);
    if (console.readConfirmation(
        "Adding %d new transactions to %s", transactions.size(), currentMonth)) {
      storage.writeMonth(updated);
    }
  }
}
