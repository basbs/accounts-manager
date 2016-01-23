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

public final class BranchTransferFormTest {
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
  private BranchTransferForm form;

  @Before
  public void setUp() {
    form = new BranchTransferForm(helper.console(), CONFIG, formHelper);
  }

  @Test
  public void testWhenMonthIsNotYetClosed_isNotApplicable() {
    AccountsMonth month = helper.newEmptyMonth();
    assertThat(form.isApplicableFor(month)).isFalse();
  }

  @Test
  public void testRenderForm() throws IOException {
    AccountsMonth month = AccountsMonth.builder()
        .setDate(YearMonth.of(2015, 10))
        .setOpeningBalance(Money.ZERO)
        .setReceiptsCarriedForward(Money.ZERO)
        .setTransactions(
            ImmutableList.of(
                Transaction.builder()
                    .setDate(31)
                    .setDescription("jw.org Transfer")
                    .setCheckingOut(Money.parse("303.03"))
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
                                .setAmount(Money.parse("101.01"))
                                .build()))
                    .build()))
        .setIsClosed(true)
        .build();

    form.generate(month);

    formHelper.assertChecked("Check Box1");
    formHelper.assertValue("Text1", "North Congregation, Anytown, California");
    formHelper.assertValue("Text5", "303.03");
    formHelper.assertValue("Text2.0.0.0", "202.02");
    formHelper.assertValue("Text2.0.0.2", "101.01");
  }
}
