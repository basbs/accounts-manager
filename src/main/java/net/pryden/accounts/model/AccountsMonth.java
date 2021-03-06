package net.pryden.accounts.model;

import autovalue.shaded.com.google.common.common.collect.Iterables;
import autovalue.shaded.com.google.common.common.collect.Lists;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
  public abstract Money openingBalance();

  /**
   * The balance of undeposited receipts carried forward from the previous month. Should always be
   * zero if the accounting instructions are being followed correctly.
   */
  @JsonProperty("receipts-carried-forward")
  public abstract Money receiptsCarriedForward();

  /** The transactions this month. */
  @JsonProperty("transactions")
  public abstract ImmutableList<Transaction> transactions();

  /**
   * Whether the month is closed. While a month is open it can have further transactions added to
   * it. But once it is closed the month-end reports can be generated.
   */
  @JsonProperty("is-closed")
  public abstract boolean isClosed();

  /** The bank reconciliation information for this month. */
  public abstract Optional<Reconciliation> reconciliation();

  @JsonProperty("reconciliation")
  @JsonInclude(Include.NON_NULL)
  @Nullable
  Reconciliation serializedReconciliation() {
    return reconciliation().orElse(null);
  }

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
      Money totalCongregationReceipts = Money.ZERO;
      Money totalWorldwideReceipts = Money.ZERO;
      Money totalReceiptsIn = Money.ZERO;
      Money totalReceiptsOut = Money.ZERO;
      Money totalCheckingIn = Money.ZERO;
      Money totalCheckingOut = Money.ZERO;
      Money totalCongregationExpenses = Money.ZERO;
      Money totalWorldwideTransfer = Money.ZERO;
      Money receiptsBalance = receiptsCarriedForward();
      Money checkingBalance = openingBalance();

      for (Transaction transaction : transactions()) {
        if (!transaction.receiptsIn().isZero()) {
          switch (transaction.category()) {
            case WORLDWIDE_WORK:
              totalWorldwideReceipts = totalWorldwideReceipts.plus(transaction.receiptsIn());
              break;

            case LOCAL_CONGREGATION_EXPENSES:
              totalCongregationReceipts = totalCongregationReceipts.plus(transaction.receiptsIn());
              break;

            default:
              throw new IllegalStateException(
                  "Unexpected receipts in value found for transaction category "
                      + transaction.category());
          }
        }
        totalReceiptsIn = totalReceiptsIn.plus(transaction.receiptsIn());
        totalReceiptsOut = totalReceiptsOut.plus(transaction.receiptsOut());
        totalCheckingIn = totalCheckingIn.plus(transaction.checkingIn());
        totalCheckingOut = totalCheckingOut.plus(transaction.checkingOut());
        if (transaction.category() == TransactionCategory.EXPENSE) {
          totalCongregationExpenses = totalCongregationExpenses.plus(transaction.checkingOut());
        }
        for (SubTransaction subTransaction : transaction.subTransactions()) {
          if (subTransaction.category() == TransactionCategory.EXPENSE) {
            totalCongregationExpenses = totalCongregationExpenses.plus(subTransaction.amount());
          }
          if (subTransaction.type()
              == BranchResolutionType.WORLDWIDE_WORK_FROM_CONTRIBUTION_BOXES) {
            totalWorldwideTransfer = totalWorldwideTransfer.plus(subTransaction.amount());
          }
        }
        receiptsBalance = receiptsBalance
            .plus(transaction.receiptsIn())
            .minus(transaction.receiptsOut());
        checkingBalance = checkingBalance
            .plus(transaction.checkingIn())
            .minus(transaction.checkingOut());
      }

      cachedComputedTotals = local = ComputedTotals.builder()
          .setTotalCongregationReceipts(totalCongregationReceipts)
          .setTotalWorldwideReceipts(totalWorldwideReceipts)
          .setTotalReceiptsIn(totalReceiptsIn)
          .setTotalReceiptsOut(totalReceiptsOut)
          .setTotalCheckingIn(totalCheckingIn)
          .setTotalCheckingOut(totalCheckingOut)
          .setTotalCongregationExpenses(totalCongregationExpenses)
          .setTotalWorldwideTransfer(totalWorldwideTransfer)
          .setReceiptsOutstandingBalance(receiptsBalance)
          .setCheckingBalance(checkingBalance)
          .setTotalOfAllBalances(receiptsBalance.plus(checkingBalance))
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
      setReconciliation(Optional.empty());
    }

    public abstract Builder setDate(YearMonth date);

    @JsonProperty("date")
    Builder setDate(String date) {
      return setDate(YearMonth.parse(date, YEAR_MONTH_FORMAT));
    }

    @JsonProperty("opening-balance")
    public abstract Builder setOpeningBalance(Money openingBalance);

    @JsonProperty("receipts-carried-forward")
    public abstract Builder setReceiptsCarriedForward(Money receiptsCarriedForward);

    public abstract Builder setTransactions(ImmutableList<Transaction> transactions);

    @JsonProperty("transactions")
    Builder setTransactions(Iterable<Transaction> transactions) {
      return setTransactions(ImmutableList.copyOf(transactions));
    }

    @JsonProperty("is-closed")
    public abstract Builder setIsClosed(boolean isClosed);

    abstract Builder setReconciliation(Optional<Reconciliation> reconciliation);

    public Builder setReconciliation(Reconciliation reconciliation) {
      return setReconciliation(Optional.of(reconciliation));
    }

    @JsonProperty("reconciliation")
    Builder setDeserializedReconciliation(@Nullable Reconciliation reconciliation) {
      return setReconciliation(Optional.ofNullable(reconciliation));
    }

    public abstract AccountsMonth build();
  }
}
