package net.pryden.accounts;

import com.google.common.primitives.Ints;
import net.pryden.accounts.Annotations.SystemIn;
import net.pryden.accounts.Annotations.SystemOut;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Helper class for working with the console.
 */
public final class Console {
  private final PrintStream out;
  // Wow, I'm using a Scanner. I never thought I'd see the day. :-)
  private final Scanner scanner;

  @Inject
  Console(@SystemIn InputStream in, @SystemOut PrintStream out) {
    this.out = out;
    this.scanner = new Scanner(in);
  }

  public void print(String message) {
    out.print(message);
  }

  public void printf(String message, Object... args) {
    out.printf(message, args);
  }

  public PrintStream out() {
    return out;
  }

  public boolean readConfirmation(String prompt, Object... args) {
    String message = String.format(prompt, args);
    out.printf("%s [Y/n] ", message);
    String line = scanner.nextLine();
    if (line.isEmpty() || line.startsWith("y") || line.startsWith("Y")) {
      return true;
    }
    out.println("Got negative response, aborting.");
    return false;
  }

  public int readInt(String prompt) {
    while (true) {
      out.print(prompt);
      String line = scanner.nextLine();
      Integer result = Ints.tryParse(line);
      if (result != null) {
        return result;
      }
      out.printf("Unable to parse \"%s\" as an integer. Please enter a different value.\n", line);
    }
  }

  public String readString(String prompt) {
    out.print(prompt);
    return scanner.nextLine();
  }

  public BigDecimal readMoney(String prompt) {
    return readMoney(prompt, null);
  }

  public BigDecimal readMoney(String prompt, @Nullable BigDecimal defaultValue) {
    while (true) {
      out.print(prompt);
      String line = scanner.nextLine();
      if (defaultValue != null && line.isEmpty()) {
        return defaultValue;
      }
      try {
        return new BigDecimal(line).setScale(2, BigDecimal.ROUND_HALF_EVEN);
      } catch (NumberFormatException ex) {
        out.printf("Unable to parse \"%s\" as a decimal. Please enter a different value.\n", line);
      }
    }
  }
}
