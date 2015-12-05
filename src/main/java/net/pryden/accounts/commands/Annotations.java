package net.pryden.accounts.commands;

import dagger.MapKey;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Package-private annotations used by this package.
 */
final class Annotations {
  Annotations() {}

  /** MapKey annotation for binding {@link Command} implementations. */
  @MapKey
  @Retention(RetentionPolicy.RUNTIME)
  @interface ForCommand {
    String value();
  }

  /**
   * Annotates an {@code ImmutableList<String>} representing the arguments to the current
   * command.
   */
  @Qualifier
  @Retention(RetentionPolicy.RUNTIME)
  @interface CommandArgs {}

  /**
   * Annotates a {@code YearMonth} object representing the current month. The value is derived
   * either from the {@code Config} or else from a command-line argument.
   */
  @Qualifier
  @Retention(RetentionPolicy.RUNTIME)
  @interface CurrentMonth {}
}
