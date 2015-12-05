package net.pryden.accounts.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import java.time.YearMonth;

/**
 * Configuration for the AccountsManager tool.
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_Config.Builder.class)
public abstract class Config {
  Config() {}

  /** The congregation name, e.g. "North Congregation". */
  @JsonProperty("congregation-name")
  public abstract String congregationName();

  /** The city the congregation is in. */
  @JsonProperty("congregation-city")
  public abstract String congregationCity();

  /** The state the congregation is in. */
  @JsonProperty("congregation-state")
  public abstract String congregationState();

  /** The path to the Accounts Sheet (S-26) PDF form template. */
  @JsonProperty("accounts-sheet-form-path")
  public abstract String accountsSheetFormPath();

  /** The path to the Record of Electronic Funds Transfer (TO-62) PDF form template. */
  @JsonProperty("funds-transfer-form-path")
  public abstract String fundsTransferFormPath();

  /** The path to the Monthly Congregation Accounts Report (S-30) PDF form template. */
  @JsonProperty("accounts-report-form-path")
  public abstract String accountsReportFormPath();

  /** The path to the root directory where each month's files are stored. */
  @JsonProperty("root-dir")
  public abstract String rootDir();

  /** The resolutions currently active for sending extra funds to the branch each month. */
  @JsonProperty("branch-resolutions")
  public abstract ImmutableList<BranchResolution> branchResolutions();

  /**
   * The current month. This is not necessarily the actual current calendar month, but rather the
   * month that is currently open and being worked on.
   */
  public abstract YearMonth currentMonth();

  @JsonProperty("current-month")
  String serializedCurrentMonth() {
    return currentMonth().toString();
  }

  /** Returns a {@link Builder} instance initialized with this object's fields. */
  public abstract Builder toBuilder();

  /** Returns a new {@link Builder} instance. */
  public static Builder builder() {
    return new AutoValue_Config.Builder();
  }

  /** Builder for {@link Config} instances. */
  @AutoValue.Builder
  public abstract static class Builder {
    Builder() {
      setBranchResolutions(ImmutableList.of());
    }

    @JsonProperty("congregation-name")
    public abstract Builder setCongregationName(String name);

    @JsonProperty("congregation-city")
    public abstract Builder setCongregationCity(String city);

    @JsonProperty("congregation-state")
    public abstract Builder setCongregationState(String state);

    @JsonProperty("accounts-sheet-form-path")
    public abstract Builder setAccountsSheetFormPath(String path);

    @JsonProperty("funds-transfer-form-path")
    public abstract Builder setFundsTransferFormPath(String path);

    @JsonProperty("accounts-report-form-path")
    public abstract Builder setAccountsReportFormPath(String path);

    @JsonProperty("root-dir")
    public abstract Builder setRootDir(String path);

    public abstract Builder setBranchResolutions(ImmutableList<BranchResolution> branchResolutions);

    @JsonProperty("branch-resolutions")
    Builder setBranchResolutions(Iterable<BranchResolution> branchResolutions) {
      return setBranchResolutions(ImmutableList.copyOf(branchResolutions));
    }

    @JsonProperty("current-month")
    Builder setCurrentMonth(String yearMonth) {
      return setCurrentMonth(YearMonth.parse(yearMonth));
    }

    public abstract Builder setCurrentMonth(YearMonth yearMonth);

    /** Builds a new {@link Config} instance. */
    public abstract Config build();
  }
}
