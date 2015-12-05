package net.pryden.accounts.reports;

import net.pryden.accounts.Console;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.Config;
import net.pryden.accounts.model.Transaction;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
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
      }
      // TODO(dpryden): Fill in page two of the form
    }

    private void writeHeader() throws IOException {
      form.setValue("Text1", config.congregationName());
      form.setValue("Text2", config.congregationCity());
      form.setValue("Text3", config.congregationState());
      form.setValue("Text4", month.date().format(MONTH_NAME));
      form.setValue("Text5", String.valueOf(month.date().getYear()));
    }

    private void writeGridLine(int index, Transaction transaction) throws IOException {
      if (index > 51) {
        throw new UnsupportedOperationException(
            "Wrapping to page 2 of the form is not yet implemented");
      }
      form.setValue("Text7.0." + index, String.valueOf(transaction.date()));
      form.setValue("Text8.0." + index, transaction.description());
      form.setValue("Text9." + index, String.valueOf(transaction.category().code()));
      form.setValue("Text10." + index, formatMoney(transaction.receiptsIn()));
      form.setValue("Text12." + index, formatMoney(transaction.receiptsOut()));
      form.setValue("Text14." + index, formatMoney(transaction.checkingIn()));
      form.setValue("Text16." + index, formatMoney(transaction.checkingOut()));
    }

    private static String formatMoney(BigDecimal value) {
      if (value.equals(BigDecimal.ZERO)) {
        return "";
      }
      return formatMoneyPreserveZero(value);
    }

    private static String formatMoneyPreserveZero(BigDecimal value) {
      return value.setScale(2, BigDecimal.ROUND_HALF_EVEN).toPlainString();
    }
  }
}
