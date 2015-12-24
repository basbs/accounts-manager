package net.pryden.accounts.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.google.common.base.Strings;
import net.pryden.accounts.model.Money.MoneyStringConverter;
import net.pryden.accounts.model.Money.StringMoneyConverter;

import java.math.BigDecimal;

/**
 * Represents an amount of money in US dollars.
 */
@JsonSerialize(converter = MoneyStringConverter.class)
@JsonDeserialize(converter = StringMoneyConverter.class)
public final class Money {
  /** A convenience constant representing zero dollars. */
  public static final Money ZERO = Money.parse("0.00");

  /**
   * Parses a string into a {@link Money} value. This method should parse any value returned from
   * {@link #toFormattedStringPreserveZero()} into an equivalent value, and can parse other formats
   * as well.
   *
   * @throws IllegalArgumentException if the string cannot be parsed
   */
  public static Money parse(String value) {
    value = value.trim();
    try {
      if (value.startsWith("(") && value.endsWith(")")) {
        return new Money(new BigDecimal(value.substring(1, value.length() - 1)).negate());
      }
      return new Money(new BigDecimal(value));
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException(
          String.format("Cannot parse string \"%s\" as an amount of money", value));
    }
  }

  private final BigDecimal value;

  private Money(BigDecimal value) {
    this.value = value.setScale(2, BigDecimal.ROUND_HALF_EVEN);
  }

  /** Returns a new Money instance that represents the sum of this amount and {@code other}. */
  public Money plus(Money other) {
    return new Money(value.add(other.value));
  }

  /** Returns a new Money instance that represents this amount minus the {@code other} amount. */
  public Money minus(Money other) {
    return new Money(value.subtract(other.value));
  }

  /** Returns a new Money instance representing the negation of this amount. */
  public Money negate() {
    return new Money(value.negate());
  }

  /** Returns whether this Money object represents a negative amount of money. */
  public boolean isNegative() {
    return value.signum() < 0;
  }

  /** Returns whether this Money object represents zero dollars. */
  public boolean isZero() {
    return value.signum() == 0;
  }

  /** Returns whether this Money object represents a positive amount of money. */
  public boolean isPositive() {
    return value.signum() > 0;
  }

  /**
   * Returns a formatted string suitable for display purposes.
   *
   * <p>If this Money object represents zero dollars, this method returns the empty string.
   */
  public String toFormattedString() {
    if (isZero()) {
      return "";
    }
    return toFormattedStringPreserveZero();
  }

  /**
   * Similar to {@link #toFormattedString()} except that it represents zero dollars as an explicit
   * "0.00" rather than the empty string.
   */
  public String toFormattedStringPreserveZero() {
    if (isNegative()) {
      return "(" + value.negate().toPlainString() + ")";
    }
    return value.toPlainString();
  }

  /**
   * Returns a string formatted to the given number of characters. This is really only useful for
   * fixed-width formatting purposes.
   */
  public String toPaddedString(int padding) {
    return Strings.padStart(toFormattedString(), padding, ' ');
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof Money
        && ((Money) other).value.equals(value);
  }

  @Override
  public String toString() {
    return toFormattedStringPreserveZero();
  }

  /** Converter class used when serializing Money values into JSON (or YAML). */
  static final class MoneyStringConverter extends StdConverter<Money, String> {
    @Override
    public String convert(Money value) {
      return value.toFormattedStringPreserveZero();
    }
  }

  /** Converter class used when deserializing Money values from JSON (or YAML). */
  static final class StringMoneyConverter extends StdConverter<String, Money> {
    @Override
    public Money convert(String value) {
      return Money.parse(value);
    }
  }
}
