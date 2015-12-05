package net.pryden.accounts.model;

import autovalue.shaded.com.google.common.common.collect.Iterables;
import autovalue.shaded.com.google.common.common.collect.Lists;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents the accounts information for a given month.
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_AccountsMonth.Builder.class)
public abstract class AccountsMonth {
  static final DateTimeFormatter YEAR_MONTH_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM");

  AccountsMonth() {}

  /** A representation of the month. */
  public abstract YearMonth date();

  @JsonProperty("date")
  String serializedDate() {
    return date().toString();
  }

  /** The balance at the beginning of the month. */
  @JsonProperty("opening-balance")
  public abstract BigDecimal openingBalance();

  /** The transactions this month. */
  @JsonProperty("transactions")
  public abstract ImmutableList<Transaction> transactions();

  /** Returns a {@link Builder} instance initialized with this object's fields. */
  public abstract Builder toBuilder();

  /**
   * Returns an updated {@link AccountsMonth} object with the given transactions added to it.
   */
  public AccountsMonth withNewTransactions(Transaction... newTransactions) {
    return withNewTransactions(Arrays.asList(newTransactions));
  }

  /**
   * Returns an updated {@link AccountsMonth} object with the given transactions added to it.
   */
  public AccountsMonth withNewTransactions(Iterable<Transaction> newTransactions) {
    AccountsMonth.Builder builder = toBuilder();
    List<Transaction> allTransactions =
        Lists.newArrayList(Iterables.concat(transactions(), newTransactions));
    Collections.sort(allTransactions);
    builder.setTransactions(ImmutableList.copyOf(allTransactions));
    return builder.build();
  }

  /** Returns a new {@link Builder} instance. */
  public static Builder builder() {
    return new AutoValue_AccountsMonth.Builder();
  }

  /** Builder for {@link AccountsMonth} instances. */
  @AutoValue.Builder
  public abstract static class Builder {
    Builder() {}

    public abstract Builder setDate(YearMonth date);

    @JsonProperty("date")
    Builder setDate(String date) {
      return setDate(YearMonth.parse(date, YEAR_MONTH_FORMAT));
    }

    public abstract Builder setOpeningBalance(BigDecimal openingBalance);

    @JsonProperty("opening-balance")
    Builder setOpeningBalance(String openingBalance) {
      return setOpeningBalance(new BigDecimal(openingBalance));
    }

    public abstract Builder setTransactions(ImmutableList<Transaction> transactions);

    @JsonProperty("transactions")
    Builder setTransactions(Iterable<Transaction> transactions) {
      return setTransactions(ImmutableList.copyOf(transactions));
    }

    public abstract AccountsMonth build();
  }
}
