package net.pryden.accounts.commands;

import com.google.common.collect.ImmutableList;
import dagger.Module;
import dagger.Provides;
import dagger.Provides.Type;
import net.pryden.accounts.CommandLineArgs;
import net.pryden.accounts.commands.Annotations.CommandArgs;
import net.pryden.accounts.commands.Annotations.ForCommand;

import javax.inject.Provider;
import java.util.Map;

/**
 * Module that binds all the {@link Command} implementations in this package.
 */
@Module
public final class CommandsModule {
  @Provides
  @CurrentCommand
  String provideCommandName(@CommandLineArgs ImmutableList<String> args) {
    if (args.isEmpty()) {
      return "help";
    }
    return args.get(0);
  }

  @Provides
  @CommandArgs
  ImmutableList<String> provideCommandArgs(@CommandLineArgs ImmutableList<String> args) {
    if (args.size() <= 1) {
      return ImmutableList.of();
    }
    return args.subList(1, args.size());
  }

  @Provides(type = Type.MAP)
  @ForCommand("help")
  Command provideHelpCommand(HelpCommand command) {
    return command;
  }

  @Provides
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
