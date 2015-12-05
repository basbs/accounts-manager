package net.pryden.accounts.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.pryden.accounts.Console;
import net.pryden.accounts.Storage;
import net.pryden.accounts.commands.Annotations.CurrentMonth;
import net.pryden.accounts.model.AccountsMonth;

import javax.inject.Inject;
import java.io.IOException;
import java.time.YearMonth;

final class DumpMonthCommand implements Command {
  private final Console console;
  private final Storage storage;
  private final YearMonth currentMonth;

  @Inject
  DumpMonthCommand(Console console, Storage storage, @CurrentMonth YearMonth currentMonth) {
    this.console = console;
    this.storage = storage;
    this.currentMonth = currentMonth;
  }

  @Override
  public void run() throws IOException {
    // TODO(dpryden): Should this dump in YAML format instead? Maybe as an option?
    AccountsMonth month = storage.readMonth(currentMonth);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    objectMapper.writeValue(console.out(), month);
  }
}
