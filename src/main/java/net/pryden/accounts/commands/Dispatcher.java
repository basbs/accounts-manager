package net.pryden.accounts.commands;

import com.beust.jcommander.JCommander;
import com.google.common.collect.ImmutableList;
import net.pryden.accounts.Args;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Factory that handles deciding which command to dispatch to.
 */
@Singleton
public final class Dispatcher {
  private final ImmutableList<String> args;

  @Inject
  Dispatcher(@Args ImmutableList<String> args) {
    this.args = args;
  }

  public Command determineCommand() {
    JCommander jc = new JCommander(this);

    jc.parse(args.toArray(new String[args.size()]));

    // TODO(dpryden): dispatch to the correct command
    // each command should have its own Args object that gets populated by JCommander
    // should each command have its own subcomponent as well?

    throw new UnsupportedOperationException();
  }
}
