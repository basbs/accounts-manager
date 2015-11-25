package net.pryden.accounts.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Represents the accounts information for a given month.
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_AccountsMonth.Builder.class)
public abstract class AccountsMonth {
  static final DateTimeFormatter YEAR_MONTH_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM");

  AccountsMonth() {}

  /** A representation of the month. */
  @JsonProperty("date")
  public abstract YearMonth date();

  /** The balance at the beginning of the month. */
  @JsonProperty("opening-balance")
  public abstract BigDecimal openingBalance();

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

    public abstract AccountsMonth build();
  }
}
