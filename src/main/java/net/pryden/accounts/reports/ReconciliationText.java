package net.pryden.accounts.reports;

import net.pryden.accounts.Console;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.Config;
import net.pryden.accounts.model.Money;
import net.pryden.accounts.model.Reconciliation;
import net.pryden.accounts.model.UnreconciledTransaction;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Generates a Reconciliation.txt file listing information about the accounts reconciliation.
 */
final class ReconciliationText implements Report {
  private static final String FILENAME = "Reconciliation.txt";

  private final Console console;
  private final Config config;

  @Inject
  ReconciliationText(Console console, Config config) {
    this.console = console;
    this.config = config;
  }

  @Override
  public boolean isApplicableFor(AccountsMonth month) {
    return month.reconciliation().isPresent();
  }

  @Override
  public void generate(AccountsMonth month) throws IOException {
    console.print("Generating " + FILENAME + "\n");
    Path outputFilePath = Paths.get(config.rootDir(), month.date().toString(), FILENAME);
    try (Writer out = Files.newBufferedWriter(outputFilePath, StandardCharsets.UTF_8)) {
      new ReconciliationTextGenerator(month, out).run();
    }
  }

  private static class ReconciliationTextGenerator {
    final AccountsMonth month;
    final Reconciliation rec;
    final Writer out;

    ReconciliationTextGenerator(AccountsMonth month, Writer out) {
      this.month = month;
      this.rec = month.reconciliation().get();
      this.out = out;
    }

    void run() throws IOException {
      printf("Bank Statement Reconciliation for %s\n\n",
          month.date().format(DateTimeFormatter.ofPattern("MMMM uuuu", Locale.US)));
      printf("Ending balance from bank statement: %s\n",
          rec.statementBalance().toFormattedStringPreserveZero());
      printf("\nDeposits not listed on statement:\n");
      Money deposits = printAndSumTransactions(rec.unreconciledTransactions()
          .stream()
          .filter(t -> t.amount().isPositive()));
      printf("\nSubtotal: %s\n",
          rec.statementBalance().plus(deposits).toFormattedStringPreserveZero());
      printf("\nOutstanding checks and withdrawals:\n");
      Money withdrawals = printAndSumTransactions(rec.unreconciledTransactions()
          .stream()
          .filter(t -> t.amount().isNegative()));
      printf("\nEnding balance: %s\n", rec.reconciledBalance().toFormattedStringPreserveZero());
      Money reconciledBalance = rec.statementBalance().plus(deposits).minus(withdrawals);
      if (rec.reconciledBalance().equals(reconciledBalance)) {
        printf("\nReconciliation: OK\n");
      } else {
        printf("\nDiscrepancy of %s\n", reconciledBalance.minus(rec.reconciledBalance()));
      }
      printf("Reconciled on %s\n", rec.dateReconciled().format(DateTimeFormatter.ISO_DATE));
    }

    private Money printAndSumTransactions(Stream<UnreconciledTransaction> stream)
        throws IOException {
      Iterator<UnreconciledTransaction> iter = stream.iterator();
      Money sum = Money.ZERO;
      while (iter.hasNext()) {
        UnreconciledTransaction transaction = iter.next();
        Money amount = transaction.amount();
        // Process all amounts as positive, both for formatting purposes and so that we can
        // subtract the withdrawals rather than adding them above.
        if (amount.isNegative()) {
          amount = amount.negate();
        }
        sum = sum.plus(amount);
        printf(String.format("    %s %s %s\n",
            transaction.date().format(DateTimeFormatter.ISO_DATE),
            amount.toPaddedString(10),
            transaction.description()));
      }
      printf("Total: %s\n", sum.toFormattedStringPreserveZero());
      return sum;
    }

    void printf(String message, Object... args) throws IOException {
      out.append(String.format(message, args));
    }
  }
}
