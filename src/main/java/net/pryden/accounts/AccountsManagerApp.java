package net.pryden.accounts;

import com.google.common.collect.ImmutableList;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import net.pryden.accounts.Annotations.UserHomeDir;
import net.pryden.accounts.commands.Command;
import net.pryden.accounts.commands.CommandsModule;
import net.pryden.accounts.commands.CurrentCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Main class that bootstraps the application.
 */
final class AccountsManagerApp implements Runnable {
  @Singleton
  @Component(modules = {
      TopLevelModule.class,
      CommandsModule.class,
      StorageModule.class
  })
  interface Root {
    AccountsManagerApp app();
  }

  @Module
  static final class TopLevelModule {
    private final ImmutableList<String> args;

    TopLevelModule(String[] args) {
      this.args = ImmutableList.copyOf(args);
    }

    @Provides
    @CommandLineArgs
    ImmutableList<String> provideArgs() {
      return args;
    }

    @Provides
    @UserHomeDir
    String provideUserHomeDir() {
      return System.getProperty("user.home");
    }
  }

  public static void main(String[] args) throws Exception {
    Root root = DaggerAccountsManagerApp_Root.builder()
        .topLevelModule(new TopLevelModule(args))
        .build();
    root.app().run();
  }

  private final Command command;

  @Inject
  AccountsManagerApp(@CurrentCommand Command command) {
    this.command = command;
  }

  @Override
  public void run() {
    command.run();
  }
}
