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
            + "    dump-month: Dump the current month in JSON format.\n"
            + "    help: Show this help.\n"
            + "\n"
            + "Common arguments:\n"
            + "    --month=YYYY-MM    Set the current month.\n"
            + "                       (if not set, the value from the config file is used \n"
            + "                       instead)\n"
            + "\n");
  }
}
