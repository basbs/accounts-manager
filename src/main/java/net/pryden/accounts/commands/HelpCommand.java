package net.pryden.accounts.commands;

import javax.inject.Inject;

final class HelpCommand implements Command {
  @Inject HelpCommand() {}

  @Override
  public void run() {
    // TODO(dpryden): It would be slick if help could be auto-generated.
    System.out.print(
        "Usage: AccountsManagerApp <command> [args]\n"
            + "\n"
            + "Commands:\n"
            + "    add-deposit: Adds a deposit to the current month.\n"
            + "    add-expense: Add an expense item to the current month.\n"
            + "    add-receipts: Add receipts to the current month.\n"
            + "    close-month: Close the current month and compute totals.\n"
            + "    dump-config: Dump the current config data to console.\n"
            + "    dump-month: Dump the current month data to console.\n"
            + "    generate-forms: Generate PDF forms in the current month's folder.\n"
            + "    reconcile: Reconcile a bank statement with the accounts data.\n"
            + "    help: Show this help.\n"
            + "\n"
            + "Common arguments:\n"
            + "    --month=YYYY-MM    Set the current month.\n"
            + "                       (if not set, the value from the config file is used \n"
            + "                       instead)\n"
            + "\n");
  }
}
