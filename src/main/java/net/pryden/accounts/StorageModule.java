package net.pryden.accounts;

import dagger.Module;
import dagger.Provides;
import net.pryden.accounts.Annotations.UserHomeDir;
import net.pryden.accounts.model.Config;

import javax.inject.Singleton;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Module that provides storage-related bindings.
 */
@Module
final class StorageModule {
  static final String CONFIG_FILE_NAME = ".accounts-manager.yaml";

  @Provides
  @Singleton
  Config provideConfig(Marshaller marshaller, @UserHomeDir String userHomeDir) {
    Path configFilePath = Paths.get(userHomeDir, CONFIG_FILE_NAME);
    // TODO(dpryden): Cope with missing config on first time bootstrap
    return marshaller.read(configFilePath, Config.class);
  }
}
