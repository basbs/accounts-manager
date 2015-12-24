package net.pryden.accounts.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a transaction that has not been reconciled with the bank statement.
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_UnreconciledTransaction.Builder.class)
public abstract class UnreconciledTransaction {
  UnreconciledTransaction() {}

  /** The date of the transaction. */
  public abstract LocalDate date();

  @JsonProperty("date")
  String serializedDate() {
    return date().format(DateTimeFormatter.ISO_DATE);
  }

  /** The description of the transaction. */
  @JsonProperty("description")
  public abstract String description();

  /**
   * The amount of this transaction. This may be positive in the case of an unreconciled deposit,
   * or negative in the case of an unreconciled expense.
   */
  @JsonProperty("amount")
  public abstract Money amount();

  /** Returns a new {@link Builder} instance. */
  public static Builder builder() {
    return new AutoValue_UnreconciledTransaction.Builder();
  }

  /** Builder for {@link UnreconciledTransaction} instances. */
  @AutoValue.Builder
  public abstract static class Builder {
    Builder() {}

    public abstract Builder setDate(LocalDate date);

    @JsonProperty("description")
    public abstract Builder setDescription(String description);

    @JsonProperty("date")
    Builder setDate(String date) {
      return setDate(LocalDate.parse(date, DateTimeFormatter.ISO_DATE));
    }

    @JsonProperty("amount")
    public abstract Builder setAmount(Money checkingIn);

    public abstract UnreconciledTransaction build();
  }
}
