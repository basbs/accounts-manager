package net.pryden.accounts.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents the bank reconciliation for a given month.
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_Reconciliation.Builder.class)
public abstract class Reconciliation {
  Reconciliation() {}

  /** The date this reconciliation was completed. */
  public abstract LocalDate dateReconciled();

  @JsonProperty("date-reconciled")
  String serializedDateReconciled() {
    return dateReconciled().format(DateTimeFormatter.ISO_DATE);
  }

  /** The balance from the bank statement. */
  @JsonProperty("statement-balance")
  public abstract BigDecimal statementBalance();

  /** The balance that was reconciled successfully. */
  @JsonProperty("reconciled-balance")
  public abstract BigDecimal reconciledBalance();

  /** The outstanding transactions that could not be reconciled. */
  @JsonProperty("unreconciled-transactions")
  public abstract ImmutableList<UnreconciledTransaction> unreconciledTransactions();

  /** Returns a new {@link Builder} instance. */
  public static Builder builder() {
    return new AutoValue_Reconciliation.Builder();
  }

  /** Builder for {@link Reconciliation} instances. */
  @AutoValue.Builder
  public abstract static class Builder {
    Builder() {}

    public abstract Builder setDateReconciled(LocalDate dateReconciled);

    @JsonProperty("date-reconciled")
    Builder setDateReconciled(String date) {
      return setDateReconciled(LocalDate.parse(date, DateTimeFormatter.ISO_DATE));
    }

    @JsonProperty("statement-balance")
    public abstract Builder setStatementBalance(BigDecimal statementBalance);

    @JsonProperty("reconciled-balance")
    public abstract Builder setReconciledBalance(BigDecimal reconciledBalance);

    abstract Builder setUnreconciledTransactions(
        ImmutableList<UnreconciledTransaction> unreconciledTransactions);

    @JsonProperty("unreconciled-transactions")
    public Builder setUnreconciledTransactions(Iterable<UnreconciledTransaction> transactions) {
      return setUnreconciledTransactions(ImmutableList.copyOf(transactions));
    }

    public abstract Reconciliation build();
  }
}
