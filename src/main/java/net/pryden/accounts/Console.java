package net.pryden.accounts;

import com.google.common.primitives.Ints;
import net.pryden.accounts.model.Money;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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
    return line.isEmpty()
        || line.startsWith("y")
        || line.startsWith("Y");
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

  /** Prints the given prompt to the console, and then reads a Money response. */
  public final Money readMoney(String prompt) {
    return readMoney(prompt, null);
  }

  /**
   * Prints the given prompt to the console, and then reads a Money response. If the user's
   * response is empty returns the {@code defaultValue} instead.
   */
  public final Money readMoney(String prompt, @Nullable Money defaultValue) {
    while (true) {
      String line = readString(prompt);
      if (defaultValue != null && line.isEmpty()) {
        return defaultValue;
      }
      try {
        return Money.parse(line);
      } catch (IllegalArgumentException ex) {
        printf("Unable to parse \"%s\" as an amount of money. Please enter a different value.\n",
            line);
      }
    }
  }

  /**
   * Prints the given prompt to the console, and then reads a LocalDate response.
   */
  public final LocalDate readDate(String prompt) {
    while (true) {
      String line = readString(prompt);
      try {
        return LocalDate.parse(line);
      } catch (DateTimeParseException ex) {
        printf("Unable to parse \"%s\" as a date. Please enter a different value.\n", line);
      }
    }
  }
}
