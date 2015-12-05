package net.pryden.accounts.commands;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotates a string representing the name of the currently selected command. Also annotates
 * a binding for a {@link Command} object for the currently selected command.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentCommand {}
