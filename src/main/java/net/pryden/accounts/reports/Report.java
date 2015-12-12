package net.pryden.accounts.reports;

import net.pryden.accounts.model.AccountsMonth;

import java.io.IOException;

/**
 * An implementation of a specific report.
 */
public interface Report {
  /**
   * Returns whether this report can be generated for the given month.
   *
   * <p>This can be used to avoid generating reports for which no data is yet available.
   */
  boolean isApplicableFor(AccountsMonth month);

  /**
   * Generates a report on disk in the given month's folder.
   */
  void generate(AccountsMonth month) throws IOException;
}
