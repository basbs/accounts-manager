package net.pryden.accounts.testing;

import com.google.common.truth.Truth;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.Config;
import net.pryden.accounts.model.Transaction;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Arrays;

/**
 * Helper utility for tests.
 */
public final class TestHelper {
  private final FakeConsole fakeConsole;
  private final FakeStorage fakeStorage;
  private YearMonth currentMonth;

  public static TestHelper create() {
    return new TestHelper();
  }

  private TestHelper() {
    this.fakeConsole = new FakeConsole();
    this.fakeStorage = new FakeStorage();
    this.currentMonth = YearMonth.of(2015, 10);
  }

  public FakeConsole console() {
    return fakeConsole;
  }

  public FakeStorage storage() {
    return fakeStorage;
  }

  public YearMonth currentMonth() {
    return currentMonth;
  }

  public void setCurrentMonth(YearMonth yearMonth) {
    this.currentMonth = yearMonth;
  }

  public Config newFakeConfig() {
    return Config.builder()
        .setCongregationName("Congregation name")
        .setCongregationCity("City")
        .setCongregationState("State")
        .setAccountsReportFormPath("!!no such path!!")
        .setFundsTransferFormPath("!!no such path!!")
        .setAccountsSheetFormPath("!!no such path!!")
        .setRootDir("!!no such path!!")
        .setCurrentMonth(YearMonth.of(2015, 10))
        .build();
  }

  public AccountsMonth newEmptyMonth() {
    return newEmptyMonth(currentMonth);
  }

  public AccountsMonth newEmptyMonth(YearMonth yearMonth) {
    return AccountsMonth.builder()
        .setDate(yearMonth)
        .setOpeningBalance(BigDecimal.ZERO)
        .setReceiptsCarriedForward(BigDecimal.ZERO)
        .build();
  }

  public void writeEmptyMonth() {
    fakeStorage.writeMonth(newEmptyMonth(currentMonth));
  }

  public void addTransactions(Transaction... transactions) {
    AccountsMonth month = fakeStorage.readMonth(currentMonth);
    month = month.withNewTransactions(transactions);
    fakeStorage.writeMonth(month);
  }

  public AccountsMonth readCurrentMonth() {
    return fakeStorage.readMonth(currentMonth);
  }

  public String getConsoleOutput() {
    return fakeConsole.getOutput();
  }

  public void assertAllTransactions(Transaction... transactions) {
    Truth.assertThat(readCurrentMonth().transactions())
        .containsExactlyElementsIn(Arrays.asList(transactions))
        .inOrder();
  }
}
