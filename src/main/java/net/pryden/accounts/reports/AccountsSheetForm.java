package net.pryden.accounts.reports;

import net.pryden.accounts.Console;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.ComputedTotals;
import net.pryden.accounts.model.Config;
import net.pryden.accounts.model.SubTransaction;
import net.pryden.accounts.model.Transaction;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents the S-26 Accounts Sheet form.
 */
public final class AccountsSheetForm {
  private static final String FILENAME = "S-26-E Accounts Sheet.pdf";

  private final Console console;
  private final Config config;

  @Inject
  AccountsSheetForm(Console console, Config config) {
    this.console = console;
    this.config = config;
  }

  public void generate(AccountsMonth month) throws IOException {
    console.print("Generating " + FILENAME + "\n");
    Path outputFilePath = Paths.get(config.rootDir(), month.date().toString(), FILENAME);
    try (FormHelper form = FormHelper.create(config.accountsSheetFormPath(), outputFilePath)) {
      new AccountsSheetGenerator(config, month, form).run();
      if (console.readConfirmation("Write %s?", outputFilePath)) {
        form.save();
      }
    }
  }

  /** Helper class to actually populate the form. */
  private static final class AccountsSheetGenerator {
    private static final DateTimeFormatter MONTH_ENDING_FORMAT =
        DateTimeFormatter.ofPattern("MMMM d, uuuu");

    private static final DateTimeFormatter MONTH_NAME =
        DateTimeFormatter.ofPattern("MMMM", Locale.US);

    private final Config config;
    private final AccountsMonth month;
    private final FormHelper form;

    AccountsSheetGenerator(Config config, AccountsMonth month, FormHelper form) {
      this.config = config;
      this.month = month;
      this.form = form;
    }

    void run() throws IOException {
      writeHeader();
      int index = 0;
      for (Transaction transaction : month.transactions()) {
        writeGridLine(index, transaction);
        index++;
        for (SubTransaction subTransaction : transaction.subTransactions()) {
          writeSubGridLine(index, subTransaction);
          index++;
        }
      }
      if (month.isClosed()) {
        ComputedTotals totals = month.computeTotals();
        writeTotals(totals);
      }
      // TODO(dpryden): Support "Obligations at end of month"
    }

    private void writeHeader() throws IOException {
      form.setValue("Text1", config.congregationName());
      form.setValue("Text2", config.congregationCity());
      form.setValue("Text3", config.congregationState());
      form.setValue("Text4", month.date().format(MONTH_NAME));
      form.setValue("Text5", String.valueOf(month.date().getYear()));
    }

    private void checkGridIndex(int index) {
      // TODO(dpryden): Support wrapping to the second page
      if (index > 51) {
        throw new UnsupportedOperationException(
            "Wrapping to page 2 of the form is not yet implemented");
      }
    }

    private void writeGridLine(int index, Transaction transaction) throws IOException {
      checkGridIndex(index);
      form.setValue("Text7.0." + index, String.valueOf(transaction.date()));
      form.setValue("Text8.0." + index, transaction.description());
      form.setValue("Text9." + index, transaction.category().codeAsString());
      form.setMoney("Text10." + index, transaction.receiptsIn());
      form.setMoney("Text12." + index, transaction.receiptsOut());
      form.setMoney("Text14." + index, transaction.checkingIn());
      form.setMoney("Text16." + index, transaction.checkingOut());
    }

    private void writeSubGridLine(int index, SubTransaction subTransaction) throws IOException {
      checkGridIndex(index);
      String description = String.format("%s [%s]",
          subTransaction.description(), FormHelper.formatMoney(subTransaction.amount()));
      form.setValue("Text8.0." + index, description);
      form.setValue("Text9." + index, subTransaction.category().codeAsString());
    }

    private void writeTotals(ComputedTotals totals) throws IOException {
      // "Totals of all columns" at bottom of page one
      form.setMoneyPreserveZero("Text11", totals.totalReceiptsIn());
      form.setMoneyPreserveZero("Text13", totals.totalReceiptsOut());
      form.setMoneyPreserveZero("Text15", totals.totalCheckingIn());
      form.setMoneyPreserveZero("Text17", totals.totalCheckingOut());

      // "Accounts sheet reconciliation" on page two
      String monthEndingDate = month.date().atEndOfMonth().format(MONTH_ENDING_FORMAT);
      form.setValue("Text38", monthEndingDate);

      // Receipts
      form.setMoneyPreserveZero("Text53", month.receiptsCarriedForward());
      form.setMoneyPreserveZero("Text27", totals.totalReceiptsIn());
      form.setMoneyPreserveZero("Text29", totals.totalReceiptsOut());
      form.setMoneyPreserveZero("Text40", totals.receiptsOutstandingBalance());

      // Checking account
      form.setMoneyPreserveZero("Text56", month.openingBalance());
      form.setMoneyPreserveZero("Text31", totals.totalCheckingIn());
      form.setMoneyPreserveZero("Text33", totals.totalCheckingOut());
      form.setMoneyPreserveZero("Text42", totals.checkingBalance());

      // Total funds at end of month
      form.setMoneyPreserveZero("Text46", totals.totalOfAllBalances());
    }
  }
}
