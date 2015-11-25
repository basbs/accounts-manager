package net.pryden.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Low-level implementation of reading or writing value objects from disk.
 *
 * <p>This implementation is just a thin wrapper over the Jackson API.
 */
@Singleton
final class Marshaller {
  private final ObjectMapper jackson;

  @Inject
  Marshaller() {
    jackson = new ObjectMapper(new YAMLFactory());
  }

  /** Reads an object from disk at the specified path. */
  <T> T read(Path path, Class<T> type) {
    try {
      return jackson.readValue(path.toFile(), type);
    } catch (IOException ex) {
      throw new MarshallingException(ex);
    }
  }

  /** Writes an object to disk at the specified path. */
  <T> void write(Path path, T value) {
    try {
      jackson.writeValue(path.toFile(), value);
    } catch (IOException ex) {
      throw new MarshallingException(ex);
    }
  }

  /** Exception that indicates an error when reading or writing objects from disk. */
  static final class MarshallingException extends RuntimeException {
    MarshallingException(Throwable t) {
      super(t);
    }
  }
}
