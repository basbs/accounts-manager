package net.pryden.accounts;

import net.pryden.accounts.Annotations.UserHomeDir;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.Config;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;

/**
 * {@link Storage} implementation that stores data in YAML files on disk.
 */
@Singleton
final class DiskStorage implements Storage {
  static final String CONFIG_FILE_NAME = ".accounts-manager.yaml";
  static final String ACCOUNTS_FILE_NAME = "accounts.yaml";

  private final Path configPath;
  private final String rootDir;
  private final Marshaller marshaller;

  @Inject
  DiskStorage(@UserHomeDir String userHomeDir, Config config, Marshaller marshaller) {
    this.configPath = Paths.get(userHomeDir, CONFIG_FILE_NAME);
    this.rootDir = config.rootDir();
    this.marshaller = marshaller;
  }

  @Override
  public AccountsMonth readMonth(YearMonth date) {
    Path monthPath = Paths.get(rootDir, date.toString(), ACCOUNTS_FILE_NAME);
    return marshaller.read(monthPath, AccountsMonth.class);
  }

  @Override
  public void writeMonth(AccountsMonth month) {
    File monthDir = Paths.get(rootDir, month.date().toString()).toFile();
    if (!monthDir.exists()) {
      monthDir.mkdirs();
    }
    Path monthPath = Paths.get(monthDir.getPath(), ACCOUNTS_FILE_NAME);
    marshaller.write(monthPath, month);
  }

  @Override
  public void updateConfig(Config updatedConfig) {
    marshaller.write(configPath, updatedConfig);
  }
}
