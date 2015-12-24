package net.pryden.accounts.commands;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.Money;
import net.pryden.accounts.model.Reconciliation;
import net.pryden.accounts.model.Transaction;
import net.pryden.accounts.model.TransactionCategory;
import net.pryden.accounts.model.UnreconciledTransaction;
import net.pryden.accounts.testing.TestHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;

@RunWith(JUnit4.class)
public final class ReconcileCommandTest {
  private static final Instant TIME = Instant.parse("2015-02-03T10:15:30.00Z");

  private TestHelper helper = TestHelper.create();
  private Clock clock = Clock.fixed(TIME, ZoneId.of("UTC"));

  @Before
  public void setUp() {
    helper.setCurrentMonth(YearMonth.of(2015, 3));
    helper.storage().updateConfig(helper.newFakeConfig());
  }

  private void run() throws Exception {
    ReconcileCommand command = new ReconcileCommand(helper.console(), helper.storage(), clock);
    command.run();
  }

  private void writePreviousMonthReconciliation(Reconciliation reconciliation) {
    helper
        .storage()
        .writeMonth(
            helper
                .newEmptyMonth()
                .toBuilder()
                .setDate(YearMonth.of(2015, 1))
                .setIsClosed(true)
                .setReconciliation(reconciliation)
                .build());
  }

  private void writeMonthWithTransactions(Transaction... transactions) {
    helper
        .storage()
        .writeMonth(
            helper.newEmptyMonth()
                .toBuilder()
                .setDate(YearMonth.of(2015, 2))
                .setTransactions(ImmutableList.copyOf(transactions))
                .setIsClosed(true)
                .build());
  }

  // TODO(dpryden): Add tests for:
  //     - reconciling carried-forward transactions
  //     - error cases

  @Test
  public void testReconcilingOnlyCurrentTransactions() throws Exception {
    writePreviousMonthReconciliation(Reconciliation.builder()
        .setDateReconciled(LocalDate.of(2015, 11, 17))
        .setReconciledBalance(Money.ZERO)
        .setStatementBalance(Money.ZERO)
        .setUnreconciledTransactions(ImmutableList.of())
        .build());

    Money reconciled1 = Money.parse("101.01");
    Money reconciled2 = Money.parse("202.02");
    Money closingBalance = reconciled1.plus(reconciled2).negate();
    Money unreconciled = Money.parse("66.11");

    writeMonthWithTransactions(
        Transaction.builder()
            .setDate(1)
            .setDescription("reconciled1")
            .setCategory(TransactionCategory.EXPENSE)
            .setCheckingOut(reconciled1)
            .build(),
        Transaction.builder()
            .setDate(2)
            .setDescription("reconciled2")
            .setCategory(TransactionCategory.EXPENSE)
            .setCheckingOut(reconciled2)
            .build(),
        Transaction.builder()
            .setDate(3)
            .setDescription("unreconciled")
            .setCategory(TransactionCategory.EXPENSE)
            .setCheckingOut(unreconciled)
            .build());

    helper.console()
        .addExpectedInput("2015-02-28" /* statement date */)
        .addExpectedInput(closingBalance.toFormattedString() /* statement closing balance */)
        .addExpectedInput("Y" /* reconciled1 */)
        .addExpectedInput("Y" /* reconciled2 */)
        .addExpectedInput("N" /* unreconciled */)
        .addExpectedInput("Y" /* confirm write month to storage */);

    run();

    assertThat(helper.getConsoleOutput()).contains("Congratulations");

    AccountsMonth month = helper.storage().readMonth(YearMonth.of(2015, 2));
    Reconciliation reconciliation = month.reconciliation().get();
    assertThat(reconciliation.dateReconciled()).isEqualTo(LocalDate.now(clock));
    assertThat(reconciliation.statementBalance()).isEqualTo(closingBalance);
    assertThat(reconciliation.reconciledBalance())
        .isEqualTo(closingBalance.minus(unreconciled));
    assertThat(reconciliation.unreconciledTransactions()).hasSize(1);
    assertThat(reconciliation.unreconciledTransactions().get(0))
        .isEqualTo(UnreconciledTransaction.builder()
            .setDate(LocalDate.of(2015, 2, 3))
            .setDescription("unreconciled")
            .setAmount(unreconciled.negate())
            .build());
  }
}
