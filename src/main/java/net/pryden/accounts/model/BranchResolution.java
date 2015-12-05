package net.pryden.accounts.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import java.math.BigDecimal;

/**
 * Represents a resolution for sending funds to the branch each month as part of the monthly
 * transfer. These are typically used for contributions to the Worldwide Work, to Kingdom Hall and
 * Assembly Hall Construction Worldwide, or to special funds like the Global Assistance Arrangement
 * and the Traveling Overseer Assistance Arrangement.
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_BranchResolution.Builder.class)
public abstract class BranchResolution {
  BranchResolution() {}

  /** The description of the transaction. */
  @JsonProperty("description")
  public abstract String description();

  /** The category of the transaction. */
  public abstract TransactionCategory category();

  @JsonProperty("category")
  String serializedCategory() {
    return category().serializedForm();
  }

  /** The type of this branch resolution. */
  @JsonProperty("type")
  public abstract BranchResolutionType type();

  /** The amount to be transferred to the branch each month. */
  @JsonProperty("amount")
  public abstract BigDecimal amount();

  /** Returns a new {@link Builder} instance. */
  public static Builder builder() {
    return new AutoValue_BranchResolution.Builder();
  }

  /** Builder for {@link BranchResolution} instances. */
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

    public abstract BranchResolution build();
  }
}
