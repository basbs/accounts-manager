package net.pryden.accounts.commands;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import dagger.Module;
import dagger.Provides;
import net.pryden.accounts.CommandLineArgs;
import net.pryden.accounts.commands.Annotations.CommandArgs;
import net.pryden.accounts.commands.Annotations.CurrentMonth;
import net.pryden.accounts.model.Config;

import javax.inject.Provider;
import javax.inject.Singleton;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

/**
 * Module that binds all the {@link Command} implementations in this package, plus various bindings
 * that they use.
 */
@Module(includes = {AllCommandsModule.class})
public final class CommandsModule {
  @Provides
  @Singleton
  @CurrentCommand
  String provideCommandName(@CommandLineArgs ImmutableList<String> args) {
    if (args.isEmpty()) {
      return "help";
    }
    return args.get(0);
  }

  @Provides
  @Singleton
  @CommandArgs
  ImmutableMap<String, String> provideCommandArgs(@CommandLineArgs ImmutableList<String> args) {
    ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
    // Skip the first arg, it's the @CurrentCommand String
    for (int i = 1; i < args.size(); i++) {
      final String arg = args.get(i);
      if (!arg.startsWith("--")) {
        throw new IllegalArgumentException("Unexpected command line arg: " + arg);
      }
      List<String> parts = Splitter.on('=').splitToList(arg.substring(2));
      if (parts.size() != 2) {
        throw new IllegalArgumentException("Unparseable command line arg: " + arg);
      }
      builder.put(parts.get(0), parts.get(1));
    }
    return builder.build();
  }

  @Provides
  @Singleton
  @CurrentMonth
  YearMonth provideCurrentMonth(Config config, @CommandArgs ImmutableMap<String, String> args) {
    if (args.containsKey("month")) {
      return YearMonth.parse(args.get("month"));
    }
    return config.currentMonth();
  }

  @Provides
  @Singleton
  @CurrentCommand
  Command provideCommand(
      @CurrentCommand String commandName,
      Map<String, Provider<Command>> commandMap) {
    Provider<Command> provider = commandMap.get(commandName);
    if (provider == null) {
      return commandMap.get("help").get();
    }
    return provider.get();
  }
}
