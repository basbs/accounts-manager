package net.pryden.accounts.commands;

import static net.pryden.accounts.model.TransactionCategory.LOCAL_CONGREGATION_EXPENSES;
import static net.pryden.accounts.model.TransactionCategory.WORLDWIDE_WORK;

import net.pryden.accounts.model.Money;
import net.pryden.accounts.model.Transaction;
import net.pryden.accounts.testing.TestHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class AddReceiptsCommandTest {
  private TestHelper helper = TestHelper.create();
  private AddReceiptsCommand command;

  @Before
  public void setUp() {
    command = new AddReceiptsCommand(helper.currentMonth(), helper.storage(), helper.console());
    helper.writeEmptyMonth();
  }

  @Test
  public void testAddingReceipts_both() throws Exception {
    helper.console()
        .addExpectedInput("12" /* day of the month */)
        .addExpectedInput("42.17" /* worldwide work */)
        .addExpectedInput("103.01" /* local congregation */)
        .addExpectedInput("Y" /* confirmation */);

    command.run();

    helper.assertAllTransactions(
        Transaction.builder()
            .setDate(12)
            .setDescription("Contributions - Worldwide Work")
            .setCategory(WORLDWIDE_WORK)
            .setReceiptsIn(Money.parse("42.17"))
            .build(),
        Transaction.builder()
            .setDate(12)
            .setDescription("Contributions - Local Congregation Expenses")
            .setCategory(LOCAL_CONGREGATION_EXPENSES)
            .setReceiptsIn(Money.parse("103.01"))
            .build());
  }

  @Test
  public void testAddingReceipts_worldwideOnly() throws Exception {
    helper.console()
        .addExpectedInput("7" /* day of the month */)
        .addExpectedInput("17.00" /* worldwide work */)
        .addExpectedInput("" /* local congregation */)
        .addExpectedInput("Y" /* confirmation */);

    command.run();

    helper.assertAllTransactions(
        Transaction.builder()
            .setDate(7)
            .setDescription("Contributions - Worldwide Work")
            .setCategory(WORLDWIDE_WORK)
            .setReceiptsIn(Money.parse("17.00"))
            .build());
  }


  @Test
  public void testAddingReceipts_localOnly() throws Exception {
    helper.console()
        .addExpectedInput("6" /* day of the month */)
        .addExpectedInput("" /* worldwide work */)
        .addExpectedInput("99.99" /* local congregation */)
        .addExpectedInput("Y" /* confirmation */);

    command.run();

    helper.assertAllTransactions(
        Transaction.builder()
            .setDate(6)
            .setDescription("Contributions - Local Congregation Expenses")
            .setCategory(LOCAL_CONGREGATION_EXPENSES)
            .setReceiptsIn(Money.parse("99.99"))
            .build());
  }
}
