package net.pryden.accounts.model;

import com.google.common.collect.ImmutableMap;

import java.util.Locale;

/**
 * Enum of possible transaction categories.
 */
public enum TransactionCategory {
  LOCAL_CONGREGATION_EXPENSES('C'),
  WORLDWIDE_WORK('W'),
  EXPENSE('E'),
  DEPOSIT('D'),
  OTHER(' ');

  private static final ImmutableMap<Character, TransactionCategory> LOOKUP;
  static {
    ImmutableMap.Builder<Character, TransactionCategory> builder = ImmutableMap.builder();
    for (TransactionCategory category : TransactionCategory.values()) {
      builder.put(category.code(), category);
    }
    LOOKUP = builder.build();
  }

  public static TransactionCategory fromCode(char code) {
    TransactionCategory category = LOOKUP.get(code);
    if (category == null) {
      throw new IllegalArgumentException("Unexpected transaction category: " + code);
    }
    return category;
  }

  public static TransactionCategory fromCode(String code) {
    return fromCode(code.toUpperCase(Locale.ENGLISH).charAt(0));
  }

  private final char code;

  TransactionCategory(char code) {
    this.code = code;
  }

  public char code() {
    return code;
  }
}
