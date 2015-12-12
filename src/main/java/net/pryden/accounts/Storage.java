package net.pryden.accounts;

import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.Config;

import java.time.YearMonth;

/**
 * Very simple data storage API.
 */
public interface Storage {
  /**
   * Reads the {@link AccountsMonth} for the given month from storage.
   */
  AccountsMonth readMonth(YearMonth date);

  /**
   * Writes the given {@link AccountsMonth} to storage.
   */
  void writeMonth(AccountsMonth month);

  /**
   * Writes an updated global configuration object.
   */
  void updateConfig(Config updatedConfig);
}
