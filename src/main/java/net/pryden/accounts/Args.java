package net.pryden.accounts;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotates an array of strings representing the current command-line arguments.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Args {}
