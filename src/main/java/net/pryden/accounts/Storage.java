package net.pryden.accounts;

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
  private static final String ACCOUNTS_FILE_NAME = "accounts.yaml";

  private final String rootDir;
  private final Marshaller marshaller;

  @Inject
  Storage(Config config, Marshaller marshaller) {
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
}
