package net.pryden.accounts;

/**
 * Implementation of {@link Console} that uses the {@link System#console()}.
 */
final class SystemConsole extends Console {
  @Override
  public void print(String message) {
    System.out.print(message);
  }

  @Override
  public String readString(String prompt) {
    return System.console().readLine(prompt);
  }
}
