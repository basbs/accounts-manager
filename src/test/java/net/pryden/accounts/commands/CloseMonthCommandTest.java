package net.pryden.accounts.commands;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.BranchResolution;
import net.pryden.accounts.model.BranchResolutionType;
import net.pryden.accounts.model.Config;
import net.pryden.accounts.model.SubTransaction;
import net.pryden.accounts.model.Transaction;
import net.pryden.accounts.model.TransactionCategory;
import net.pryden.accounts.testing.TestHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
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
    BigDecimal worldwideResolutionAmount = new BigDecimal("100.00");
    String gaaResolutionDescription = "{{GAA resolution}}";
    BigDecimal gaaResolutionAmount = new BigDecimal("75.00");

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

    BigDecimal worldwide1 = new BigDecimal("101.01");
    BigDecimal worldwide2 = new BigDecimal("202.02");
    BigDecimal local1 = new BigDecimal("66.11");
    BigDecimal local2 = new BigDecimal("33.77");

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
            .setReceiptsOut(worldwide1.add(local1))
            .setCheckingIn(worldwide1.add(local1))
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
            .setReceiptsOut(worldwide2.add(local2))
            .setCheckingIn(worldwide2.add(local2))
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
            .add(worldwide2)
            .add(worldwideResolutionAmount)
            .add(gaaResolutionAmount));

    // Verify the sub-transactions
    assertThat(transfer.subTransactions())
        .containsExactly(
            SubTransaction.builder()
                .setDescription("Worldwide Work")
                .setCategory(TransactionCategory.WORLDWIDE_WORK)
                .setType(BranchResolutionType.WORLDWIDE_WORK_FROM_CONTRIBUTION_BOXES)
                .setAmount(worldwide1.add(worldwide2))
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
