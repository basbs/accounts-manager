package net.pryden.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Very simple data storage API, based on YAML files.
 */
final class Storage {
  static final String CONFIG_FILE_NAME = ".accounts-manager.yaml";

  private final ObjectMapper jackson;
  private final Path configFilePath;

  Storage(String userHomePath) {
    jackson = new ObjectMapper(new YAMLFactory());
    configFilePath = Paths.get(userHomePath, CONFIG_FILE_NAME);
  }

  Config readConfig() throws IOException {
    return jackson.readValue(configFilePath.toFile(), Config.class);
  }

  void writeConfig(Config config) throws IOException {
    jackson.writeValue(configFilePath.toFile(), config);
  }
}
