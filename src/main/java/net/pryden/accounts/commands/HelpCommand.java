package net.pryden.accounts.commands;

import javax.inject.Inject;

final class HelpCommand implements Command {
  @Inject HelpCommand() {}

  @Override
  public void run() {
    System.out.print(
        "Usage: AccountsManagerApp <command> [args]\n"
            + "\n"
            + "Commands:\n"
            + "    help: Show this help."
            + "\n");
  }
}
