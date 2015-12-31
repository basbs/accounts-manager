/**
 * Data model types for use by the rest of the application.
 *
 * <p>Each of these types is an {@code @AutoValue}, and is annotated with Jackson annotations to
 * describe how it is serialized and deserialized.
 *
 * <p>This package also contains the utility {@link net.pryden.accounts.model.Money} type which
 * is used to store currency values.
 */
@ParametersAreNonnullByDefault
package net.pryden.accounts.model;

import javax.annotation.ParametersAreNonnullByDefault;
