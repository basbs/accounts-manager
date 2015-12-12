package net.pryden.accounts;

import static com.google.common.truth.Truth.assertThat;

import net.pryden.accounts.model.Config;
import net.pryden.accounts.testing.FakeConsole;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;

@RunWith(JUnit4.class)
public final class MarshallerTest {
  @Rule public final TemporaryFolder temp = new TemporaryFolder();

  private static final Config SAMPLE_CONFIG = Config.builder()
      .setCongregationName("North Congregation")
      .setCongregationCity("Anytown")
      .setCongregationState("California")
      .setAccountsSheetFormPath("/home/user/forms/S-26-E.pdf")
      .setFundsTransferFormPath("/home/user/forms/TO-62-E.pdf")
      .setAccountsReportFormPath("/home/user/forms/S-30-E.pdf")
      .setRootDir("/home/user/accounts")
      .setCurrentMonth(YearMonth.of(2015, 11))
      .build();

  private static final String SAMPLE_CONFIG_STRING = ""
      + "congregation-name: North Congregation\n"
      + "congregation-city: Anytown\n"
      + "congregation-state: California\n"
      + "accounts-sheet-form-path: /home/user/forms/S-26-E.pdf\n"
      + "funds-transfer-form-path: /home/user/forms/TO-62-E.pdf\n"
      + "accounts-report-form-path: /home/user/forms/S-30-E.pdf\n"
      + "root-dir: /home/user/accounts\n"
      + "current-month: 2015-11\n";

  private Marshaller marshaller;
  private Path path;

  @Before
  public void setUp() throws IOException {
    marshaller = new Marshaller(new FakeConsole());
    path = temp.newFile("temp_config.yaml").toPath();
  }

  @Test
  public void testConfigRoundTrip() {
    marshaller.write(path, SAMPLE_CONFIG);

    Config roundTrip = marshaller.read(path, Config.class);
    assertThat(roundTrip).isEqualTo(SAMPLE_CONFIG);
  }

  @Test
  public void testReadSampleConfig() throws Exception {
    Files.write(path, SAMPLE_CONFIG_STRING.getBytes(StandardCharsets.UTF_8));

    Config config = marshaller.read(path, Config.class);
    assertThat(config).isEqualTo(SAMPLE_CONFIG);
  }
}
