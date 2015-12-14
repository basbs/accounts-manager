package net.pryden.accounts.testing;

import net.pryden.accounts.Storage;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.Config;

import javax.annotation.Nullable;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Fake {@link Storage} implementation for tests.
 */
public final class FakeStorage implements Storage {
  private final Map<YearMonth, AccountsMonth> storage = new HashMap<>();
  private Optional<Config> config;

  public FakeStorage() {
    this(null);
  }

  public FakeStorage(@Nullable Config config) {
    this.config = Optional.ofNullable(config);
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
    return config.get();
  }

  @Override
  public void updateConfig(Config updatedConfig) {
    this.config = Optional.of(updatedConfig);
  }
}
