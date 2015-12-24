package net.pryden.accounts.commands;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.BranchResolution;
import net.pryden.accounts.model.BranchResolutionType;
import net.pryden.accounts.model.Config;
import net.pryden.accounts.model.Money;
import net.pryden.accounts.model.SubTransaction;
import net.pryden.accounts.model.Transaction;
import net.pryden.accounts.model.TransactionCategory;
import net.pryden.accounts.testing.TestHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.YearMonth;

@RunWith(JUnit4.class)
public final class CloseMonthCommandTest {
  private TestHelper helper = TestHelper.create();
  private AddDepositCommand addDepositCommand = null;
  private GenerateFormsCommand generateFormsCommand = null;

  @Before
  public void setUp() {
    helper.setCurrentMonth(YearMonth.of(2015, 10));
    helper.storage().updateConfig(helper.newFakeConfig());
    helper.writeEmptyMonth();
  }

  private void run() throws Exception {
    CloseMonthCommand command = new CloseMonthCommand(
        helper.console(),
        helper.storage().getConfig(),
        helper.storage(),
        helper.currentMonth(),
        () -> addDepositCommand,
        () -> generateFormsCommand);
    command.run();
  }

  // TODO(dpryden): Add tests for:
  //    - adding a deposit when closing
  //    - carrying receipts forward to the next month

  @Test
  public void testClosingMonth() throws Exception {
    String worldwideResolutionDescription = "{{worldwide resolution}}";
    Money worldwideResolutionAmount = Money.parse("100.00");
    String gaaResolutionDescription = "{{GAA resolution}}";
    Money gaaResolutionAmount = Money.parse("75.00");

    Config config = helper.storage().getConfig();
    config = config.toBuilder()
        .setBranchResolutions(
            ImmutableList.of(
                BranchResolution.builder()
                    .setDescription(worldwideResolutionDescription)
                    .setType(BranchResolutionType.WORLDWIDE_WORK_RESOLUTION)
                    .setCategory(TransactionCategory.EXPENSE)
                    .setAmount(worldwideResolutionAmount)
                    .build(),
                BranchResolution.builder()
                    .setDescription(gaaResolutionDescription)
                    .setType(BranchResolutionType.GLOBAL_ASSISTANCE_ARRANGEMENT)
                    .setCategory(TransactionCategory.EXPENSE)
                    .setAmount(gaaResolutionAmount)
                    .build()))
        .build();
    helper.storage().updateConfig(config);

    Money worldwide1 = Money.parse("101.01");
    Money worldwide2 = Money.parse("202.02");
    Money local1 = Money.parse("66.11");
    Money local2 = Money.parse("33.77");

    YearMonth monthToClose = helper.currentMonth();

    helper.addTransactions(
        Transaction.builder()
            .setDate(1)
            .setDescription("worldwide1")
            .setCategory(TransactionCategory.WORLDWIDE_WORK)
            .setReceiptsIn(worldwide1)
            .build(),
        Transaction.builder()
            .setDate(1)
            .setDescription("local1")
            .setCategory(TransactionCategory.LOCAL_CONGREGATION_EXPENSES)
            .setReceiptsIn(local1)
            .build(),
        Transaction.builder()
            .setDate(2)
            .setDescription("deposit1")
            .setCategory(TransactionCategory.DEPOSIT)
            .setReceiptsOut(worldwide1.plus(local1))
            .setCheckingIn(worldwide1.plus(local1))
            .build(),
        Transaction.builder()
            .setDate(3)
            .setDescription("worldwide2")
            .setCategory(TransactionCategory.WORLDWIDE_WORK)
            .setReceiptsIn(worldwide2)
            .build(),
        Transaction.builder()
            .setDate(3)
            .setDescription("local2")
            .setCategory(TransactionCategory.LOCAL_CONGREGATION_EXPENSES)
            .setReceiptsIn(local2)
            .build(),
        Transaction.builder()
            .setDate(4)
            .setDescription("deposit2")
            .setCategory(TransactionCategory.DEPOSIT)
            .setReceiptsOut(worldwide2.plus(local2))
            .setCheckingIn(worldwide2.plus(local2))
            .build());

    helper.console()
        .addExpectedInput("Y" /* confirm closing month */)
        .addExpectedInput("N" /* don't generate PDFs */);

    run();

    // Assert that the current month has been rolled over
    assertThat(helper.storage().getConfig().currentMonth()).isEqualTo(monthToClose.plusMonths(1));

    AccountsMonth month = helper.storage().readMonth(monthToClose);
    assertThat(month.isClosed()).isTrue();

    // Verify that we wrote one more transaction
    assertThat(month.transactions()).hasSize(7);
    Transaction transfer = month.transactions().get(6);

    // Verify the transaction
    assertThat(transfer.date()).isEqualTo(31);
    assertThat(transfer.description()).isEqualTo("jw.org Transfer");
    assertThat(transfer.category()).isEqualTo(TransactionCategory.OTHER);
    assertThat(transfer.checkingOut())
        .isEqualTo(worldwide1
            .plus(worldwide2)
            .plus(worldwideResolutionAmount)
            .plus(gaaResolutionAmount));

    // Verify the sub-transactions
    assertThat(transfer.subTransactions())
        .containsExactly(
            SubTransaction.builder()
                .setDescription("Worldwide Work")
                .setCategory(TransactionCategory.WORLDWIDE_WORK)
                .setType(BranchResolutionType.WORLDWIDE_WORK_FROM_CONTRIBUTION_BOXES)
                .setAmount(worldwide1.plus(worldwide2))
                .build(),
            SubTransaction.builder()
                .setDescription(worldwideResolutionDescription)
                .setCategory(TransactionCategory.EXPENSE)
                .setType(BranchResolutionType.WORLDWIDE_WORK_RESOLUTION)
                .setAmount(worldwideResolutionAmount)
                .build(),
            SubTransaction.builder()
                .setDescription(gaaResolutionDescription)
                .setCategory(TransactionCategory.EXPENSE)
                .setType(BranchResolutionType.GLOBAL_ASSISTANCE_ARRANGEMENT)
                .setAmount(gaaResolutionAmount)
                .build());
  }
}
