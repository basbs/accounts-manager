package net.pryden.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

/**
 * Configuration for the AccountsManager tool.
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_Config.Builder.class)
abstract class Config {
  Config() {}

  /** The congregation name, e.g. "North Congregation". */
  @JsonProperty("congregation-name")
  abstract String congregationName();

  /** The city the congregation is in. */
  @JsonProperty("congregation-city")
  abstract String congregationCity();

  /** The state the congregation is in. */
  @JsonProperty("congregation-state")
  abstract String congregationState();

  /** The path to the Accounts Sheet (S-26) PDF form template. */
  @JsonProperty("accounts-sheet-form-path")
  abstract String accountsSheetFormPath();

  /** The path to the Record of Electronic Funds Transfer (TO-62) PDF form template. */
  @JsonProperty("funds-transfer-form-path")
  abstract String fundsTransferFormPath();

  /** The path to the Montly Congregation Accounts Report (S-30) PDF form template. */
  @JsonProperty("accounts-report-form-path")
  abstract String accountsReportFormPath();

  /** Returns a new {@link Builder} instance. */
  static Builder builder() {
    return new AutoValue_Config.Builder();
  }

  /** Builder for {@link Config} instances. */
  @AutoValue.Builder
  abstract static class Builder {
    Builder() {}

    @JsonProperty("congregation-name")
    abstract Builder setCongregationName(String name);

    @JsonProperty("congregation-city")
    abstract Builder setCongregationCity(String city);

    @JsonProperty("congregation-state")
    abstract Builder setCongregationState(String state);

    @JsonProperty("accounts-sheet-form-path")
    abstract Builder setAccountsSheetFormPath(String path);

    @JsonProperty("funds-transfer-form-path")
    abstract Builder setFundsTransferFormPath(String path);

    @JsonProperty("accounts-report-form-path")
    abstract Builder setAccountsReportFormPath(String path);

    /** Builds a new {@link Config} instance. */
    abstract Config build();
  }
}
