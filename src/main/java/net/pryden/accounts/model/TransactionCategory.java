package net.pryden.accounts.model;

import com.google.common.collect.ImmutableMap;

import java.util.Locale;

/**
 * Enum of possible transaction categories.
 */
public enum TransactionCategory {
  WORLDWIDE_WORK('W', 1),
  LOCAL_CONGREGATION_EXPENSES('C', 2),
  EXPENSE('E', 3),
  DEPOSIT('D', 4),
  OTHER(' ', 5);

  private static final ImmutableMap<Character, TransactionCategory> LOOKUP;
  static {
    ImmutableMap.Builder<Character, TransactionCategory> builder = ImmutableMap.builder();
    for (TransactionCategory category : TransactionCategory.values()) {
      builder.put(category.code, category);
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
    if (code.equals("None")) {
      return OTHER;
    }
    return fromCode(code.toUpperCase(Locale.US).charAt(0));
  }

  private final char code;
  private final int ordering;

  TransactionCategory(char code, int ordering) {
    this.code = code;
    this.ordering = ordering;
  }

  public String codeAsString() {
    return String.valueOf(code);
  }

  public String serializedForm() {
    if (this == OTHER) {
      return "None";
    }
    return String.valueOf(code);
  }
}
