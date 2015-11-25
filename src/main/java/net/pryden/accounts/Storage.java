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
final class Storage {
  private final String rootDir;
  private final Marshaller marshaller;

  @Inject
  Storage(Config config, Marshaller marshaller) {
    this.rootDir = config.rootDir();
    this.marshaller = marshaller;
  }

  AccountsMonth readMonth(YearMonth date) {
    Path monthPath = Paths.get(rootDir, date.toString());
    return marshaller.read(monthPath, AccountsMonth.class);
  }

  void writeMonth(AccountsMonth month) {
    Path monthPath = Paths.get(rootDir, month.date().toString());
    marshaller.write(monthPath, month);
  }
}
