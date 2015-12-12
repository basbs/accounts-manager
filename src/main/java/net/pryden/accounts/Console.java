package net.pryden.accounts;

import com.google.common.primitives.Ints;

import javax.annotation.Nullable;
import java.math.BigDecimal;

/**
 * Helper API for working with the console.
 */
public abstract class Console {
  /** Prints the given string to the console. */
  public abstract void print(String message);

  /** Prints the given formatted string to the console. */
  public final void printf(String message, Object... args) {
    print(String.format(message, args));
  }

  /** Prints the given formatted prompt to the console, then reads a boolean response. */
  public final boolean readConfirmation(String prompt, Object... args) {
    String message = String.format(prompt, args);
    String line = readString(String.format("%s [Y/n] ", message));
    if (line.isEmpty() || line.startsWith("y") || line.startsWith("Y")) {
      return true;
    }
    print("Got negative response, aborting.\n");
    return false;
  }

  /** Prints the given prompt to the console, then reads an integer response. */
  public final int readInt(String prompt) {
    while (true) {
      String line = readString(prompt);
      Integer result = Ints.tryParse(line);
      if (result != null) {
        return result;
      }
      printf("Unable to parse \"%s\" as an integer. Please enter a different value.\n", line);
    }
  }

  /** Prints the given prompt to the console, and then reads a string response. */
  public abstract String readString(String prompt);

  /** Prints the given prompt to the console, and then reads a BigDecimal response. */
  public final BigDecimal readMoney(String prompt) {
    return readMoney(prompt, null);
  }

  /**
   * Prints the given prompt to the console, and then reads a BigDecimal response. If the user's
   * response is empty returns the {@code defaultValue} instead.
   */
  public final BigDecimal readMoney(String prompt, @Nullable BigDecimal defaultValue) {
    while (true) {
      String line = readString(prompt);
      if (defaultValue != null && line.isEmpty()) {
        return defaultValue;
      }
      try {
        return new BigDecimal(line).setScale(2, BigDecimal.ROUND_HALF_EVEN);
      } catch (NumberFormatException ex) {
        printf("Unable to parse \"%s\" as a decimal. Please enter a different value.\n", line);
      }
    }
  }
}
