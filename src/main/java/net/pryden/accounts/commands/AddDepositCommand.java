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
    BigDecimal receiptsBalance = BigDecimal.ZERO;
    for (Transaction transaction : month.transactions()) {
      receiptsBalance = receiptsBalance
          .add(transaction.receiptsIn())
          .subtract(transaction.receiptsOut());

    }
    if (receiptsBalance.equals(BigDecimal.ZERO)) {
      if (!console.readConfirmation("There is currently no receipts balance for %s. "
          + "Are you sure you want to make a deposit?", currentMonth)) {
        return;
      }
    }
    int date = console.readInt("Date (day of the month): ");
    BigDecimal amount = console.readMoney(
        String.format("Amount to deposit [%s]: ", receiptsBalance),
        receiptsBalance);

    AccountsMonth updated = month.withNewTransactions(Transaction.builder()
        .setDate(date)
        .setDescription("Deposit to checking account")
        .setCategory(TransactionCategory.DEPOSIT)
        .setReceiptsOut(amount)
        .setCheckingIn(amount)
        .build());
    if (console.readConfirmation("Adding new transaction to %s", currentMonth)) {
      storage.writeMonth(updated);
    }
  }
}
