package net.pryden.accounts;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Helper class for working with the console.
 *
 * <p>Implementation note: it would have been nice to have been able to use {@link java.io.Console}
 * here, but that class does not provide any test seams, so simply depending on {@link InputStream}
 * and {@link PrintStream} actually makes testing easier. However, now that I've extracted a
 * {@link Console} interface it could be the test seam.
 */
public final class ConsoleImpl extends Console {
  private final PrintStream out;
  private final Scanner scanner;

  ConsoleImpl(InputStream in, PrintStream out) {
    this.out = out;
    this.scanner = new Scanner(in);
  }

  @Override
  public void print(String message) {
    out.print(message);
  }

  @Override
  public String readString(String prompt) {
    out.print(prompt);
    return scanner.nextLine();
  }

}
