package net.pryden.accounts.reports;

import net.pryden.accounts.Console;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.Config;
import net.pryden.accounts.model.SubTransaction;
import net.pryden.accounts.model.Transaction;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Represents a TO-62 Record of Electronic Funds Transfer form.
 */
final class BranchTransferForm implements Report {
  private static final String FILENAME = "TO-62-E Record of Electronic Funds Transfer.pdf";

  private final Console console;
  private final Config config;

  @Inject
  BranchTransferForm(Console console, Config config) {
    this.console = console;
    this.config = config;
  }

  @Override
  public boolean isApplicableFor(AccountsMonth month) {
    return month.isClosed();
  }

  @Override
  public void generate(AccountsMonth month) throws IOException {
    console.print("Generating " + FILENAME + "\n");
    Path outputFilePath = Paths.get(config.rootDir(), month.date().toString(), FILENAME);
    try (FormHelper form = FormHelper.create(config.fundsTransferFormPath(), outputFilePath)) {
      new BranchTransferFormGenerator(config, month, form).run();
      console.printf("Writing %s\n", outputFilePath);
      form.save();
    }
  }

  /** Helper class to actually populate the form. */
  private static final class BranchTransferFormGenerator {
    private final Config config;
    private final AccountsMonth month;
    private final FormHelper form;

    BranchTransferFormGenerator(Config config, AccountsMonth month, FormHelper form) {
      this.config = config;
      this.month = month;
      this.form = form;
    }

    void run() throws IOException {
      populateHeader();
      Transaction transaction = findTransaction();
      populateTransaction(transaction);
    }

    private void populateHeader() throws IOException {
      form.setCheckBox("Check Box1", true);
      String formattedCongregationName =
          String.format("%s, %s, %s",
              config.congregationName(),
              config.congregationCity(),
              config.congregationState());
      form.setValue("Text1", formattedCongregationName);
    }

    private Transaction findTransaction() {
      for (Transaction transaction : month.transactions().reverse()) {
        if (!transaction.subTransactions().isEmpty()) {
          return transaction;
        }
      }
      throw new IllegalStateException("TO-62 form cannot be rendered "
          + "because there is no branch transaction yet for this month.");
    }

    private void populateTransaction(Transaction transaction) throws IOException {
      form.setMoney("Text5", transaction.checkingOut());
      for (SubTransaction subTransaction : transaction.subTransactions()) {
        switch (subTransaction.type()) {
          case WORLDWIDE_WORK_FROM_CONTRIBUTION_BOXES:
            form.setMoney("Text2.0.0.0", subTransaction.amount());
            break;

          case KINGDOM_HALL_AND_ASSEMBLY_HALL_WORLDWIDE:
            form.setMoney("Text2.0.0.2", subTransaction.amount());
            break;

          default:
            throw new UnsupportedOperationException(
                "Rendering " + subTransaction.type() + " is not yet implemented.");
        }
      }
    }
  }
}
