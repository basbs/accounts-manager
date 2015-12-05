package net.pryden.accounts;

import net.pryden.accounts.Annotations.UserHomeDir;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.Config;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;

/**
 * Very simple data storage API.
 */
@Singleton
public final class Storage {
  static final String CONFIG_FILE_NAME = ".accounts-manager.yaml";
  private static final String ACCOUNTS_FILE_NAME = "accounts.yaml";

  private final Path configPath;
  private final String rootDir;
  private final Marshaller marshaller;

  @Inject
  Storage(@UserHomeDir String userHomeDir, Config config, Marshaller marshaller) {
    this.configPath = Paths.get(userHomeDir, CONFIG_FILE_NAME);
    this.rootDir = config.rootDir();
    this.marshaller = marshaller;
  }

  public AccountsMonth readMonth(YearMonth date) {
    Path monthPath = Paths.get(rootDir, date.toString(), ACCOUNTS_FILE_NAME);
    return marshaller.read(monthPath, AccountsMonth.class);
  }

  public void writeMonth(AccountsMonth month) {
    Path monthPath = Paths.get(rootDir, month.date().toString(), ACCOUNTS_FILE_NAME);
    marshaller.write(monthPath, month);
  }

  public void updateConfig(Config updatedConfig) {
    marshaller.write(configPath, updatedConfig);
  }
}
