package net.pryden.accounts.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;

/**
 * Represents a single transaction (one line in the accounts sheet).
 *
 * <p>Note: This implementation only supports a single account (the optional extra bank account on
 * the accounts sheet is not yet implemented.)
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_Transaction.Builder.class)
public abstract class Transaction implements Comparable<Transaction> {
  Transaction() {}

  /** The day of the month. */
  @JsonProperty("date")
  public abstract int date();

  /** The description of the transaction. */
  @JsonProperty("description")
  public abstract String description();

  /** The category of the transaction. */
  public abstract TransactionCategory category();

  @JsonProperty("category")
  String serializedCategory() {
    return category().serializedForm();
  }

  /** The value of money received as receipts. */
  @JsonProperty("receipts-in")
  public abstract Money receiptsIn();

  /** The value of money being deposited or otherwise spent out of receipts. */
  @JsonProperty("receipts-out")
  public abstract Money receiptsOut();

  /** The value of money being deposited into the checking account. */
  @JsonProperty("checking-in")
  public abstract Money checkingIn();

  /** The value of money being spent out of the checking account. */
  @JsonProperty("checking-out")
  public abstract Money checkingOut();

  /** Returns whether this transaction has zero value (either in or out) in the receipts columns. */
  public boolean isZeroReceipts() {
    return receiptsIn().isZero() && receiptsOut().isZero();
  }

  /** Returns whether this transaction has zero value (either in or out) in the checking columns. */
  public boolean isZeroChecking() {
    return checkingIn().isZero() && checkingOut().isZero();
  }

  /**
   * Sub-transactions of this transaction. Only branch transfers typically have these.
   */
  @JsonProperty("sub-transactions")
  public abstract ImmutableList<SubTransaction> subTransactions();

  @Override
  public int compareTo(Transaction other) {
    return ComparisonChain.start()
        .compare(date(), other.date())
        .compare(category(), other.category())
        .result();
  }

  /** Returns a new {@link Builder} instance. */
  public static Builder builder() {
    return new AutoValue_Transaction.Builder();
  }

  /** Builder for {@link Transaction} instances. */
  @AutoValue.Builder
  public abstract static class Builder {
    Builder() {
      // Default values
      setReceiptsIn(Money.ZERO);
      setReceiptsOut(Money.ZERO);
      setCheckingIn(Money.ZERO);
      setCheckingOut(Money.ZERO);
      setSubTransactions(ImmutableList.of());
    }

    @JsonProperty("date")
    public abstract Builder setDate(int date);

    @JsonProperty("description")
    public abstract Builder setDescription(String description);

    @JsonProperty("category")
    Builder setCategory(String category) {
      return setCategory(TransactionCategory.fromCode(category));
    }

    public abstract Builder setCategory(TransactionCategory category);

    @JsonProperty("receipts-in")
    public abstract Builder setReceiptsIn(Money receiptsIn);

    @JsonProperty("receipts-out")
    public abstract Builder setReceiptsOut(Money receiptsOut);

    @JsonProperty("checking-in")
    public abstract Builder setCheckingIn(Money checkingIn);

    @JsonProperty("checking-out")
    public abstract Builder setCheckingOut(Money checkingOut);

    public abstract Builder setSubTransactions(ImmutableList<SubTransaction> subTransactions);

    @JsonProperty("sub-transactions")
    Builder setSubTransactions(Iterable<SubTransaction> subTransactions) {
      return setSubTransactions(ImmutableList.copyOf(subTransactions));
    }

    public abstract Transaction build();
  }
}
