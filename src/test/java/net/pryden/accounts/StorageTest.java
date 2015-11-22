package net.pryden.accounts;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RunWith(JUnit4.class)
public final class StorageTest {
  @Rule public final TemporaryFolder temp = new TemporaryFolder();

  private static final Config SAMPLE_CONFIG = Config.builder()
      .setCongregationName("North Congregation")
      .setCongregationCity("Anytown")
      .setCongregationState("California")
      .setAccountsSheetFormPath("/home/user/forms/S-26-E.pdf")
      .setFundsTransferFormPath("/home/user/forms/TO-62-E.pdf")
      .setAccountsReportFormPath("/home/user/forms/S-30-E.pdf")
      .build();

  private static final String SAMPLE_CONFIG_STRING = ""
      + "congregation-name: North Congregation\n"
      + "congregation-city: Anytown\n"
      + "congregation-state: California\n"
      + "accounts-sheet-form-path: /home/user/forms/S-26-E.pdf\n"
      + "funds-transfer-form-path: /home/user/forms/TO-62-E.pdf\n"
      + "accounts-report-form-path: /home/user/forms/S-30-E.pdf\n";

  @Test
  public void testConfigRoundTrip() throws Exception {
    Storage storage = new Storage(temp.getRoot().getPath());
    storage.writeConfig(SAMPLE_CONFIG);

    Config roundTrip = storage.readConfig();
    assertThat(roundTrip).isEqualTo(SAMPLE_CONFIG);
  }

  @Test
  public void testReadSampleConfig() throws Exception {
    Path configPath = Paths.get(temp.getRoot().getPath(), Storage.CONFIG_FILE_NAME);
    Files.write(configPath, SAMPLE_CONFIG_STRING.getBytes(StandardCharsets.UTF_8));

    Storage storage = new Storage(temp.getRoot().getPath());
    Config roundTrip = storage.readConfig();
    assertThat(roundTrip).isEqualTo(SAMPLE_CONFIG);
  }
}
