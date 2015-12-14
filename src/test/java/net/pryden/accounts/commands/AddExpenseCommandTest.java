package net.pryden.accounts.commands;

import net.pryden.accounts.model.Transaction;
import net.pryden.accounts.model.TransactionCategory;
import net.pryden.accounts.testing.TestHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;

@RunWith(JUnit4.class)
public final class AddExpenseCommandTest {
  private TestHelper helper = TestHelper.create();
  private AddExpenseCommand command;

  @Before
  public void setUp() {
    command = new AddExpenseCommand(helper.console(), helper.storage(), helper.currentMonth());
    helper.writeEmptyMonth();
  }

  @Test
  public void testAddExpense() throws Exception {
    int date = 3;
    String description = "{{transaction description}}";
    BigDecimal amount = new BigDecimal("42.17");

    helper.console()
        .addExpectedInput(String.valueOf(date) /* day of the month */)
        .addExpectedInput(description /* transaction description */)
        .addExpectedInput(amount.toPlainString() /* amount */)
        .addExpectedInput("Y" /* confirmation */);

    command.run();

    helper.assertAllTransactions(
        Transaction.builder()
            .setDate(date)
            .setDescription(description)
            .setCategory(TransactionCategory.EXPENSE)
            .setCheckingOut(amount)
            .build());
  }
}
