package net.pryden.accounts.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import java.math.BigDecimal;

/**
 * Represents a sub-transaction (an added line under an existing transaction). Only branch transfers
 * typically have these.
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_SubTransaction.Builder.class)
public abstract class SubTransaction {
  SubTransaction() {}

  /** The description of the transaction. */
  @JsonProperty("description")
  public abstract String description();

  /** The category of the transaction. */
  public abstract TransactionCategory category();

  @JsonProperty("category")
  String serializedCategory() {
    return category().serializedForm();
  }

  /** The branch resolution type of this transaction, if any. */
  @JsonProperty("type")
  public abstract BranchResolutionType type();

  /** The amount of this sub-transaction. */
  @JsonProperty("amount")
  public abstract BigDecimal amount();

  /** Returns a new {@link Builder} instance. */
  public static Builder builder() {
    return new AutoValue_SubTransaction.Builder();
  }

  /** Builder for {@link SubTransaction} instances. */
  @AutoValue.Builder
  public abstract static class Builder {
    Builder() {
      setCategory(TransactionCategory.EXPENSE);
    }

    @JsonProperty("description")
    public abstract Builder setDescription(String description);

    @JsonProperty("category")
    Builder setCategory(String category) {
      return setCategory(TransactionCategory.fromCode(category));
    }

    public abstract Builder setCategory(TransactionCategory category);

    @JsonProperty("type")
    public abstract Builder setType(BranchResolutionType type);

    @JsonProperty("amount")
    public abstract Builder setAmount(BigDecimal amount);

    public abstract SubTransaction build();
  }
}
