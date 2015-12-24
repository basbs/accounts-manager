package net.pryden.accounts.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

/**
 * Represents the totals of the various columns computed from the month's data. It is never
 * serialized -- instead the values are recomputed when necessary.
 */
@AutoValue
public abstract class ComputedTotals {
  /** Total of all "Local Congregation Expenses" receipts. */
  @JsonProperty("total-congregation-receipts")
  public abstract Money totalCongregationReceipts();

  /** Total of all "Worldwide Work" receipts. */
  @JsonProperty("total-worldwide-receipts")
  public abstract Money totalWorldwideReceipts();

  /** Total value of money received as receipts over the month. */
  @JsonProperty("total-receipts-in")
  public abstract Money totalReceiptsIn();

  /** Total value of money deposited or otherwise spent out of receipts over the month. */
  @JsonProperty("total-receipts-out")
  public abstract Money totalReceiptsOut();

  /** Total value of money deposited into the checking account over the month. */
  @JsonProperty("total-checking-in")
  public abstract Money totalCheckingIn();

  /** Total value of money spent out of the checking account over the month. */
  @JsonProperty("total-checking-out")
  public abstract Money totalCheckingOut();

  /**
   * Total value of money earmarked for the Worldwide Work and transferred to the branch. If
   * procedures are being followed correctly, this should always be equal to
   * {@link #totalWorldwideReceipts()}.
   */
  @JsonProperty("total-worldwide-transfer")
  public abstract Money totalWorldwideTransfer();

  /**
   * Total value of congregation expenses (that is, transactions with
   * {@link TransactionCategory#EXPENSE}) throughout the month.
   */
  @JsonProperty("total-congregation-expenses")
  public abstract Money totalCongregationExpenses();

  /**
   * Outstanding balance of receipts collected but not deposited. At the end of the month this
   * should always be zero.
   */
  @JsonProperty("receipts-outstanding-balance")
  public abstract Money receiptsOutstandingBalance();

  /** Checking account balance at the end of the month. */
  @JsonProperty("checking-balance")
  public abstract Money checkingBalance();

  /** Sum of all balances across all accounts (receipts and checking). */
  @JsonProperty("total-of-all-balances")
  public abstract Money totalOfAllBalances();

  /** Returns a new {@link Builder} instance. */
  public static Builder builder() {
    return new AutoValue_ComputedTotals.Builder();
  }

  /** Builder for {@link ComputedTotals} instances. */
  @AutoValue.Builder
  public static abstract class Builder {
    Builder() {}

    public abstract Builder setTotalCongregationReceipts(Money totalCongregationReceipts);

    public abstract Builder setTotalWorldwideReceipts(Money totalWorldwideReceipts);

    public abstract Builder setTotalReceiptsIn(Money totalReceiptsIn);

    public abstract Builder setTotalReceiptsOut(Money totalReceiptsOut);

    public abstract Builder setTotalCheckingIn(Money totalCheckingIn);

    public abstract Builder setTotalCheckingOut(Money totalCheckingOut);

    public abstract Builder setTotalWorldwideTransfer(Money totalWorldwideTransfer);

    public abstract Builder setTotalCongregationExpenses(Money totalCongregationExpenses);

    public abstract Builder setReceiptsOutstandingBalance(Money receiptsOutstandingBalance);

    public abstract Builder setCheckingBalance(Money checkingBalance);

    public abstract Builder setTotalOfAllBalances(Money totalOfAllBalances);

    public abstract ComputedTotals build();
  }
}
