package net.pryden.accounts.commands;

import static com.google.common.truth.Truth.assertThat;

import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.Money;
import net.pryden.accounts.model.Transaction;
import net.pryden.accounts.model.TransactionCategory;
import net.pryden.accounts.testing.TestHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class AddDepositCommandTest {
  private TestHelper helper = TestHelper.create();
  private AddDepositCommand command;

  @Before
  public void setUp() {
    command = new AddDepositCommand(helper.console(), helper.storage(), helper.currentMonth());
  }

  @Test
  public void testNoReceiptsBalance() throws Exception {
    helper.writeEmptyMonth();
    helper.console().addExpectedInput("N");

    command.run();

    assertThat(helper.readCurrentMonth().transactions()).isEmpty();
    assertThat(helper.getConsoleOutput()).contains("There is currently no receipts balance");
  }

  @Test
  public void testDepositExistingReceipts() throws Exception {
    Money receipts = Money.parse("103.00");
    helper.writeEmptyMonth();
    helper.addTransactions(
        Transaction.builder()
            .setDate(1)
            .setDescription("Testing receipts")
            .setCategory(TransactionCategory.WORLDWIDE_WORK)
            .setReceiptsIn(receipts)
            .build());

    helper.console()
        .addExpectedInput(/* day of the month*/ "2")
        .addExpectedInput(/* description (default) */ "")
        .addExpectedInput(/* amount to deposit (default) */ "")
        .addExpectedInput(/* confirmation */ "Y");

    command.run();

    AccountsMonth month = helper.readCurrentMonth();
    assertThat(month.transactions()).hasSize(2);
    Transaction transaction = month.transactions().get(1);
    assertThat(transaction).isEqualTo(Transaction.builder()
        .setDate(2)
        .setDescription("Deposit to checking account")
        .setCategory(TransactionCategory.DEPOSIT)
        .setReceiptsOut(receipts)
        .setCheckingIn(receipts)
        .build());

    assertThat(helper.getConsoleOutput()).contains("Amount to deposit [103.00]: ");
  }
}
