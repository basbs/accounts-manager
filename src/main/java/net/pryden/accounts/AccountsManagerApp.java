package net.pryden.accounts;

import com.google.common.collect.ImmutableList;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import net.pryden.accounts.Annotations.UserHomeDir;
import net.pryden.accounts.commands.Dispatcher;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Main class that bootstraps the application.
 */
final class AccountsManagerApp implements Runnable {
  @Singleton
  @Component(modules = {
      TopLevelModule.class,
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
    @Args
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

  private final Dispatcher dispatcher;

  @Inject
  AccountsManagerApp(Dispatcher dispatcher) {
    this.dispatcher = dispatcher;
  }

  @Override
  public void run() {
    dispatcher.determineCommand().run();
  }
}
