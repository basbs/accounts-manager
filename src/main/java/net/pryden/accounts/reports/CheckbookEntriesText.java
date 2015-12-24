package net.pryden.accounts.reports;

import com.google.common.base.Strings;
import net.pryden.accounts.Console;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.Config;
import net.pryden.accounts.model.Money;
import net.pryden.accounts.model.Transaction;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Generates a CheckbookEntries.txt file listing entries as they should be in the checkbook.
 */
final class CheckbookEntriesText implements Report {
  private static final String FILENAME = "CheckbookEntries.txt";

  private final Console console;
  private final Config config;

  @Inject
  CheckbookEntriesText(Console console, Config config) {
    this.console = console;
    this.config = config;
  }

  @Override
  public boolean isApplicableFor(AccountsMonth month) {
    return true;
  }

  @Override
  public void generate(AccountsMonth month) throws IOException {
    console.print("Generating " + FILENAME + "\n");
    Path outputFilePath = Paths.get(config.rootDir(), month.date().toString(), FILENAME);
    try (Writer out = Files.newBufferedWriter(outputFilePath, StandardCharsets.UTF_8)) {
      Money balance = month.openingBalance();
      out.append(buildLine(null, "Opening balance", null, null, balance));

      for (Transaction transaction : month.transactions()) {
        LocalDate date = month.date().atDay(transaction.date());
        if (transaction.checkingIn().isZero() && transaction.checkingOut().isZero()) {
          continue;
        }
        balance = balance.plus(transaction.checkingIn()).minus(transaction.checkingOut());
        out.append(
            buildLine(
                date,
                transaction.description(),
                transaction.checkingOut(),
                transaction.checkingIn(),
                balance));
      }
    }
  }

  private static final DateTimeFormatter DATE_FORMAT =
      DateTimeFormatter.ofPattern("MM/dd", Locale.US);

  private String buildLine(
      @Nullable LocalDate date,
      String description,
      @Nullable Money checkingOut,
      @Nullable Money checkingIn,
      Money balance) {
    StringBuilder sb = new StringBuilder();
    if (date == null) {
      sb.append("      ");
    } else {
      sb.append(Strings.padStart(date.format(DATE_FORMAT), 6, ' '));
    }
    sb.append("  ");
    sb.append(Strings.padEnd(description, 50, ' '));
    sb.append(getPaddedMoney(checkingOut));
    sb.append(getPaddedMoney(checkingIn));
    sb.append(getPaddedMoney(balance));
    sb.append("\n");
    return sb.toString();
  }

  private String getPaddedMoney(@Nullable Money amount) {
    if (amount == null) {
      return "          ";
    }
    return amount.toPaddedString(10);
  }
}
