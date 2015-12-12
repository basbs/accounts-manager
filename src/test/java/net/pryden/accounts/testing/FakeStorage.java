package net.pryden.accounts.testing;

import net.pryden.accounts.Storage;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.Config;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

/**
 * Fake {@link Storage} implementation for tests.
 */
public final class FakeStorage implements Storage {
  private final Map<YearMonth, AccountsMonth> storage = new HashMap<>();
  private Config config;

  public FakeStorage(Config config) {
    this.config = config;
  }

  @Override
  public AccountsMonth readMonth(YearMonth date) {
    AccountsMonth result = storage.get(date);
    if (result == null) {
      throw new RuntimeException(
          String.format("No AccountsMonth for %s has been stored yet", date));
    }
    return result;
  }

  @Override
  public void writeMonth(AccountsMonth month) {
    storage.put(month.date(), month);
  }

  public Config getConfig() {
    return config;
  }

  @Override
  public void updateConfig(Config updatedConfig) {
    this.config = updatedConfig;
  }
}
