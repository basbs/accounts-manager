package net.pryden.accounts.model;

import com.google.auto.value.AutoValue;

import java.math.BigDecimal;

/**
 * Represents the totals of the various columns computed from the month's data. It is never
 * serialized -- instead the values are recomputed when necessary.
 */
@AutoValue
public abstract class ComputedTotals {
  /** Total of all "Local Congregation Expenses" receipts. */
  public abstract BigDecimal totalCongregationReceipts();

  /** Total of all "Worldwide Work" receipts. */
  public abstract BigDecimal totalWorldwideReceipts();

  /** Total value of money received as receipts over the month. */
  public abstract BigDecimal totalReceiptsIn();

  /** Total value of money deposited or otherwise spent out of receipts over the month. */
  public abstract BigDecimal totalReceiptsOut();

  /** Total value of money deposited into the checking account over the month. */
  public abstract BigDecimal totalCheckingIn();

  /** Total value of money spent out of the checking account over the month. */
  public abstract BigDecimal totalCheckingOut();

  /**
   * Outstanding balance of receipts collected but not deposited. At the end of the month this
   * should always be zero.
   */
  public abstract BigDecimal receiptsOutstandingBalance();

  /** Checking account balance at the end of the month. */
  public abstract BigDecimal checkingBalance();

  /** Sum of all balances across all accounts (receipts and checking). */
  public abstract BigDecimal totalOfAllBalances();

  /** Returns a new {@link Builder} instance. */
  public static Builder builder() {
    return new AutoValue_ComputedTotals.Builder();
  }

  /** Builder for {@link ComputedTotals} instances. */
  @AutoValue.Builder
  public static abstract class Builder {
    Builder() {}

    public abstract Builder setTotalCongregationReceipts(BigDecimal totalCongregationReceipts);

    public abstract Builder setTotalWorldwideReceipts(BigDecimal totalWorldwideReceipts);

    public abstract Builder setTotalReceiptsIn(BigDecimal totalReceiptsIn);

    public abstract Builder setTotalReceiptsOut(BigDecimal totalReceiptsOut);

    public abstract Builder setTotalCheckingIn(BigDecimal totalCheckingIn);

    public abstract Builder setTotalCheckingOut(BigDecimal totalCheckingOut);

    public abstract Builder setReceiptsOutstandingBalance(BigDecimal receiptsOutstandingBalance);

    public abstract Builder setCheckingBalance(BigDecimal checkingBalance);

    public abstract Builder setTotalOfAllBalances(BigDecimal totalOfAllBalances);

    public abstract ComputedTotals build();
  }
}
