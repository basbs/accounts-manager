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

  /**
   * The balance of undeposited receipts carried forward from the previous month. Should always be
   * zero if the accounting instructions are being followed correctly.
   */
  @JsonProperty("receipts-carried-forward")
  public abstract BigDecimal receiptsCarriedForward();

  /** The transactions this month. */
  @JsonProperty("transactions")
  public abstract ImmutableList<Transaction> transactions();

  /**
   * Whether the month is closed. While a month is open it can have further transactions added to
   * it. But once it is closed the month-end reports can be generated.
   */
  @JsonProperty("is-closed")
  public abstract boolean isClosed();

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

  /**
   * Cached {@link ComputedTotals} instance. Since this object is immutable we don't need to
   * recompute it each time it's requested. Note that, since this is a private field, AutoValue
   * doesn't know about it and it won't be serialized.
   */
  private ComputedTotals cachedComputedTotals;

  /** Computes monthly totals of all columns. */
  public ComputedTotals computeTotals() {
    // Probably not necessary, but using a local variable prevents a subtle race condition if this
    // method is ever invoked from multiple threads.
    ComputedTotals local = cachedComputedTotals;
    if (local == null) {
      BigDecimal totalCongregationReceipts = BigDecimal.ZERO;
      BigDecimal totalWorldwideReceipts = BigDecimal.ZERO;
      BigDecimal totalReceiptsIn = BigDecimal.ZERO;
      BigDecimal totalReceiptsOut = BigDecimal.ZERO;
      BigDecimal totalCheckingIn = BigDecimal.ZERO;
      BigDecimal totalCheckingOut = BigDecimal.ZERO;
      BigDecimal receiptsBalance = receiptsCarriedForward();
      BigDecimal checkingBalance = openingBalance();

      for (Transaction transaction : transactions()) {
        if (!transaction.receiptsIn().equals(BigDecimal.ZERO)) {
          switch (transaction.category()) {
            case WORLDWIDE_WORK:
              totalWorldwideReceipts = totalWorldwideReceipts.add(transaction.receiptsIn());
              break;

            case LOCAL_CONGREGATION_EXPENSES:
              totalCongregationReceipts = totalCongregationReceipts.add(transaction.receiptsIn());
              break;

            default:
              throw new IllegalStateException(
                  "Unexpected receipts in value found for transaction category "
                      + transaction.category());
          }
        }
        totalReceiptsIn = totalReceiptsIn.add(transaction.receiptsIn());
        totalReceiptsOut = totalReceiptsOut.add(transaction.receiptsOut());
        totalCheckingIn = totalCheckingIn.add(transaction.checkingIn());
        totalCheckingOut = totalCheckingOut.add(transaction.checkingOut());
        receiptsBalance = receiptsBalance
            .add(transaction.receiptsIn())
            .subtract(transaction.receiptsOut());
        checkingBalance = checkingBalance
            .add(transaction.checkingIn())
            .subtract(transaction.checkingOut());
      }

      cachedComputedTotals = local = ComputedTotals.builder()
          .setTotalCongregationReceipts(totalCongregationReceipts)
          .setTotalWorldwideReceipts(totalWorldwideReceipts)
          .setTotalReceiptsIn(totalReceiptsIn)
          .setTotalReceiptsOut(totalReceiptsOut)
          .setTotalCheckingIn(totalCheckingIn)
          .setTotalCheckingOut(totalCheckingOut)
          .setReceiptsOutstandingBalance(receiptsBalance)
          .setCheckingBalance(checkingBalance)
          .setTotalOfAllBalances(receiptsBalance.add(checkingBalance))
          .build();
    }
    return local;
  }

  /** Returns a new {@link Builder} instance. */
  public static Builder builder() {
    return new AutoValue_AccountsMonth.Builder();
  }

  /** Builder for {@link AccountsMonth} instances. */
  @AutoValue.Builder
  public abstract static class Builder {
    Builder() {
      // Default values
      setTransactions(ImmutableList.of());
      setIsClosed(false);
    }

    public abstract Builder setDate(YearMonth date);

    @JsonProperty("date")
    Builder setDate(String date) {
      return setDate(YearMonth.parse(date, YEAR_MONTH_FORMAT));
    }

    @JsonProperty("opening-balance")
    public abstract Builder setOpeningBalance(BigDecimal openingBalance);

    @JsonProperty("receipts-carried-forward")
    public abstract Builder setReceiptsCarriedForward(BigDecimal receiptsCarriedForward);

    public abstract Builder setTransactions(ImmutableList<Transaction> transactions);

    @JsonProperty("transactions")
    Builder setTransactions(Iterable<Transaction> transactions) {
      return setTransactions(ImmutableList.copyOf(transactions));
    }

    @JsonProperty("is-closed")
    public abstract Builder setIsClosed(boolean isClosed);

    public abstract AccountsMonth build();
  }
}
