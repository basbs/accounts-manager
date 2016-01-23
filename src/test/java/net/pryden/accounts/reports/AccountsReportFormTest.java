package net.pryden.accounts.reports;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.BranchResolutionType;
import net.pryden.accounts.model.Config;
import net.pryden.accounts.model.Money;
import net.pryden.accounts.model.SubTransaction;
import net.pryden.accounts.model.Transaction;
import net.pryden.accounts.model.TransactionCategory;
import net.pryden.accounts.testing.TestHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.YearMonth;

public final class AccountsReportFormTest {
  private static final Config CONFIG = Config.builder()
      .setCongregationName("North Congregation")
      .setCongregationCity("Anytown")
      .setCongregationState("California")
      .setRootDir("/tmp/bogus")
      .setAccountsReportFormPath("/tmp/bogus/1.pdf")
      .setAccountsSheetFormPath("/tmp/bogus/2.pdf")
      .setFundsTransferFormPath("/tmp/bogus/3.pdf")
      .setCurrentMonth(YearMonth.of(2015, 10))
      .build();

  private TestHelper helper = TestHelper.create();
  private FakeFormHelper formHelper = new FakeFormHelper();
  private AccountsReportForm form;

  @Before
  public void setUp() {
    helper.setCurrentMonth(YearMonth.of(2015, 10));
    form = new AccountsReportForm(helper.console(), CONFIG, formHelper);
  }

  @Test
  public void testWhenMonthIsNotYetClosed_isNotApplicable() {
    AccountsMonth month = helper.newEmptyMonth();
    assertThat(form.isApplicableFor(month)).isFalse();
  }

  // TODO(dpryden): This gives us good line coverage, but we need better branch coverage too

  @Test
  public void testRenderForm() throws IOException {
    AccountsMonth month = AccountsMonth.builder()
        .setDate(YearMonth.of(2015, 10))
        .setOpeningBalance(Money.ZERO)
        .setReceiptsCarriedForward(Money.ZERO)
        .setTransactions(
            ImmutableList.of(
                Transaction.builder()
                    .setDate(1)
                    .setDescription("Contributions - Worldwide Work")
                    .setCategory(TransactionCategory.WORLDWIDE_WORK)
                    .setReceiptsIn(Money.parse("202.02"))
                    .build(),
                Transaction.builder()
                    .setDate(1)
                    .setDescription("Contributions - Local Congregation Expenses")
                    .setCategory(TransactionCategory.LOCAL_CONGREGATION_EXPENSES)
                    .setReceiptsIn(Money.parse("300.00"))
                    .build(),
                Transaction.builder()
                    .setDate(7)
                    .setDescription("Contributions - Local Congregation Expenses")
                    .setCategory(TransactionCategory.LOCAL_CONGREGATION_EXPENSES)
                    .setReceiptsIn(Money.parse("700.00"))
                    .build(),
                Transaction.builder()
                    .setDate(10)
                    .setDescription("Deposit to checking account")
                    .setCategory(TransactionCategory.DEPOSIT)
                    .setReceiptsOut(Money.parse("1202.02"))
                    .setCheckingIn(Money.parse("1202.02"))
                    .build(),
                Transaction.builder()
                    .setDate(17)
                    .setDescription("Operating expenses")
                    .setCategory(TransactionCategory.EXPENSE)
                    .setCheckingOut(Money.parse("500.00"))
                    .build(),
                Transaction.builder()
                    .setDate(31)
                    .setDescription("jw.org Transfer")
                    .setCheckingOut(Money.parse("302.02"))
                    .setCategory(TransactionCategory.OTHER)
                    .setSubTransactions(
                        ImmutableList.of(
                            SubTransaction.builder()
                                .setDescription("Worldwide Work")
                                .setCategory(TransactionCategory.WORLDWIDE_WORK)
                                .setType(
                                    BranchResolutionType.WORLDWIDE_WORK_FROM_CONTRIBUTION_BOXES)
                                .setAmount(Money.parse("202.02"))
                                .build(),
                            SubTransaction.builder()
                                .setDescription("KH and AH Construction Worldwide")
                                .setCategory(TransactionCategory.EXPENSE)
                                .setType(
                                    BranchResolutionType.KINGDOM_HALL_AND_ASSEMBLY_HALL_WORLDWIDE)
                                .setAmount(Money.parse("100.00"))
                                .build()))
                    .build()))
        .setIsClosed(true)
        .build();

    form.generate(month);

    formHelper.assertValue("Text1", "North Congregation, Anytown, California");
    formHelper.assertValue("Text2", "October 2015");

    // Congregation contributions
    formHelper.assertValue("Text12", "1000.00");

    // Congregation expenses
    formHelper.assertValue("Text29", "600.00");

    // Funds at beginning of month
    formHelper.assertValue("Text3", "0.00");
    formHelper.assertValue("Text40", "0.00");

    // Surplus/deficit
    formHelper.assertValue("Text30", "400.00");

    // Funds at end of month
    formHelper.assertValue("Text39", "400.00");
    formHelper.assertValue("Text51", "400.00");
  }

}
