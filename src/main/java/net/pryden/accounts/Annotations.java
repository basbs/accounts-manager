package net.pryden.accounts;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Common annotations used by classes in this package. */
final class Annotations {
  private Annotations() {}

  @Qualifier
  @Retention(RetentionPolicy.RUNTIME)
  @interface SystemIn {}

  @Qualifier
  @Retention(RetentionPolicy.RUNTIME)
  @interface SystemOut {}

  @Qualifier
  @Retention(RetentionPolicy.RUNTIME)
  @interface UserHomeDir {}
}
