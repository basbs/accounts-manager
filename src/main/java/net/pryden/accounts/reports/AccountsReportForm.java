package net.pryden.accounts.reports;

import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.pryden.accounts.Console;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.ComputedTotals;
import net.pryden.accounts.model.Config;
import net.pryden.accounts.model.Money;
import net.pryden.accounts.model.Transaction;
import net.pryden.accounts.model.TransactionCategory;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Represents the S-30 Monthly Congregation Accounts Report form.
 */
final class AccountsReportForm implements Report {
  private static final String FILENAME = "S-30-E Monthly Congregation Accounts Report.pdf";

  private final Console console;
  private final Config config;
  private final FormHelper.Factory factory;

  @Inject
  AccountsReportForm(Console console, Config config, FormHelper.Factory factory) {
    this.console = console;
    this.config = config;
    this.factory = factory;
  }

  @Override
  public boolean isApplicableFor(AccountsMonth month) {
    return month.isClosed();
  }

  @Override
  public void generate(AccountsMonth month) throws IOException {
    console.print("Generating " + FILENAME + "\n");
    Path outputFilePath = Paths.get(config.rootDir(), month.date().toString(), FILENAME);
    try (FormHelper form = factory.create(config.accountsReportFormPath(), outputFilePath)) {
      new AccountsReportGenerator(config, month, form).run();
      console.printf("Writing %s\n", outputFilePath);
      form.save();
    }
  }

  /** Helper class to actually populate the form. */
  private static final class AccountsReportGenerator {
    private static final DateTimeFormatter MONTH_FORMAT =
        DateTimeFormatter.ofPattern("MMMM uuuu", Locale.US);

    private static final DateTimeFormatter MONTH_NAME =
        DateTimeFormatter.ofPattern("MMMM", Locale.US);

    private final Config config;
    private final AccountsMonth month;
    private final ComputedTotals totals;
    private final FormHelper form;

    AccountsReportGenerator(
        Config config,
        AccountsMonth month,
        FormHelper form) {
      this.config = config;
      this.month = month;
      this.totals = month.computeTotals();
      this.form = form;
    }

    void run() throws IOException {
      populateHeader();
      populateReceipts();
      populateExpenditures();
      populateFundsAvailable();
      populateReconciliation();
      populateAnnouncement();
    }

    private void populateHeader() throws IOException {
      String formattedCongregationName =
          String.format("%s, %s, %s",
              config.congregationName(),
              config.congregationCity(),
              config.congregationState());
      form.setValue("Text1", formattedCongregationName);
      form.setValue("Text2", month.date().format(MONTH_FORMAT));
    }

    private void populateReceipts() throws IOException {
      // TODO(dpryden): Handle other receipt types
      form.setValue("Text4", "Contributions in \"Local Congregation Expenses\" box");
      form.setMoneyPreserveZero("Text5", totals.totalCongregationReceipts());
      form.setMoneyPreserveZero("Text12", totals.totalCongregationReceipts());
    }

    private void populateExpenditures() throws IOException {
      Multimap<String, Expenditure> groupedExpenditures =
          MultimapBuilder.treeKeys().arrayListValues().build();
      for (Transaction transaction : month.transactions()) {
        if (transaction.category() == TransactionCategory.EXPENSE) {
          groupedExpenditures.put(
              transaction.summaryDescription(),
              Expenditure.create(transaction.summaryDescription(), transaction.checkingOut()));
        }
        transaction.subTransactions()
            .stream()
            .filter(t -> t.category() == TransactionCategory.EXPENSE)
            .forEach(t ->
                groupedExpenditures.put(
                    t.description(),
                    Expenditure.create(t.description(), t.amount())));
      }
      List<Expenditure> expenditures = new ArrayList<>();
      for (String description : groupedExpenditures.keySet()) {
        Money amount = groupedExpenditures.get(description).stream()
            .map(Expenditure::amount)
            .reduce(Money.ZERO, Money::plus);
        expenditures.add(Expenditure.create(description, amount));
      }

      int index = 13;
      for (Expenditure expenditure : expenditures) {
        writeExpenditureLine(index, expenditure.description(), expenditure.amount());
        index += 2;
      }
      form.setMoneyPreserveZero("Text29", totals.totalCongregationExpenses());
    }

    private void writeExpenditureLine(int index, String description, Money amount)
        throws IOException {
      if (index > 27) {
        throw new IllegalStateException("Too many expenditure lines to fit");
      }
      form.setValue("Text" + index, description);
      form.setMoneyPreserveZero("Text" + (index + 1), amount);
    }

    private void populateFundsAvailable() throws IOException {
      // TODO(dpryden): Handle the case where we have non-congregation funds carried over from a
      // previous month. This isn't supposed to happen anyway, but we should handle it if it does.
      Money congregationFundsAtBeginningOfMonth = month.openingBalance();
      form.setMoneyPreserveZero("Text3", congregationFundsAtBeginningOfMonth);
      Money surplusOrDeficit =
          totals.totalCongregationReceipts().minus(totals.totalCongregationExpenses());
      form.setMoneyPreserveZero("Text30", surplusOrDeficit);
      Money congregationFundsAtEndOfMonth =
          congregationFundsAtBeginningOfMonth.plus(surplusOrDeficit);
      Preconditions.checkState(congregationFundsAtEndOfMonth.equals(totals.checkingBalance()),
          "Congregation funds at end of month (%s) does not equal checking balance (%s)",
          congregationFundsAtEndOfMonth, totals.checkingBalance());
      form.setMoneyPreserveZero("Text31", congregationFundsAtEndOfMonth);
      // TODO(dpryden): Handle congregation funds reserved for special purposes
      form.setMoneyPreserveZero("Text39", congregationFundsAtEndOfMonth);
    }

    private void populateReconciliation() throws IOException {
      // Total Funds at Beginning of Month
      Money totalFundsAtBeginningOfMonth =
          month.openingBalance().plus(month.receiptsCarriedForward());
      form.setMoneyPreserveZero("Text40", totalFundsAtBeginningOfMonth);

      // All Receipts
      form.setMoneyPreserveZero("Text41", totals.totalCongregationReceipts());
      form.setMoneyPreserveZero("Text42", totals.totalWorldwideReceipts());
      // TODO(dpryden): Handle other receipt types
      Money totalReceipts =
          totals.totalCongregationReceipts().plus(totals.totalWorldwideReceipts());
      Preconditions.checkState(totalReceipts.equals(totals.totalReceiptsIn()),
          "Total of worldwide and congregation receipts (%s) does not match total receipts (%s)",
          totalReceipts, totals.totalReceiptsIn());
      form.setMoneyPreserveZero("Text45", totalReceipts);

      // All Disbursements
      form.setMoneyPreserveZero("Text46", totals.totalCongregationExpenses());
      form.setMoneyPreserveZero("Text47", totals.totalWorldwideTransfer());
      Money totalDisbursements =
          totals.totalWorldwideTransfer().plus(totals.totalCongregationExpenses());
      Preconditions.checkState(totalDisbursements.equals(totals.totalCheckingOut()),
          "Total disbursements (%s) does not match total out of checking account (%s) ",
          totalDisbursements, totals.totalCheckingOut());
      form.setMoneyPreserveZero("Text50", totalDisbursements);

      // Total Funds at End of Month
      Money totalFundsAtEndOfMonth =
          totalFundsAtBeginningOfMonth.plus(totalReceipts).minus(totalDisbursements);
      Preconditions.checkState(totalFundsAtEndOfMonth.equals(totals.totalOfAllBalances()),
          "Total funds at end of month (%s) does not match final balance (%s)",
          totalFundsAtEndOfMonth, totals.totalOfAllBalances());
      form.setMoneyPreserveZero("Text51", totalFundsAtEndOfMonth);
    }

    private void populateAnnouncement() throws IOException {
      form.setValue("Text53", month.date().format(MONTH_NAME));
      form.setMoneyPreserveZero("Text54", totals.totalCongregationReceipts());
      form.setMoneyPreserveZero("Text55", totals.totalCongregationExpenses());
      form.setMoneyPreserveZero("Text56", totals.totalOfAllBalances());
      form.setMoneyPreserveZero("Text57", totals.totalWorldwideTransfer());
    }
  }

  /** Simple holder class for a description and amount. */
  @AutoValue
  abstract static class Expenditure {
    static Expenditure create(String description, Money amount) {
      return new AutoValue_AccountsReportForm_Expenditure(description, amount);
    }

    abstract String description();
    abstract Money amount();
  }
}
