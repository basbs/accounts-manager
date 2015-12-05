package net.pryden.accounts.commands;

import dagger.Module;
import dagger.Provides;
import dagger.Provides.Type;
import net.pryden.accounts.commands.Annotations.ForCommand;

/**
 * Module that provides the MapKey binding for all the commands in this package.
 */
// TODO(dpryden): It would be really slick if this module could be auto-generated.
@Module
public final class AllCommandsModule {
  @Provides(type = Type.MAP)
  @ForCommand("add-deposit")
  Command provideAddDepositCommand(AddDepositCommand command) {
    return command;
  }

  @Provides(type = Type.MAP)
  @ForCommand("add-expense")
  Command provideAddExpenseCommand(AddExpenseCommand command) {
    return command;
  }

  @Provides(type = Type.MAP)
  @ForCommand("add-receipts")
  Command provideAddReceiptsCommand(AddReceiptsCommand command) {
    return command;
  }

  @Provides(type = Type.MAP)
  @ForCommand("dump-config")
  Command provideDumpConfigCommand(DumpConfigCommand command) {
    return command;
  }

  @Provides(type = Type.MAP)
  @ForCommand("dump-month")
  Command provideDumpMonthCommand(DumpMonthCommand command) {
    return command;
  }

  @Provides(type = Type.MAP)
  @ForCommand("generate-forms")
  Command provideGenerateFormsCommand(GenerateFormsCommand command) {
    return command;
  }

  @Provides(type = Type.MAP)
  @ForCommand("help")
  Command provideHelpCommand(HelpCommand command) {
    return command;
  }
}
