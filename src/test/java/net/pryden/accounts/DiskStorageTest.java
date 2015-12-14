package net.pryden.accounts;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.io.Files;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.model.Config;
import net.pryden.accounts.testing.TestHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.YearMonth;

@RunWith(JUnit4.class)
public final class DiskStorageTest {
  @Rule public final TemporaryFolder temp = new TemporaryFolder();

  private TestHelper helper = TestHelper.create();
  private File homeFolder;
  private File storageFolder;
  private Config config;
  private DiskStorage storage;

  @Before
  public void setUp() {
    homeFolder = temp.newFolder("home");
    storageFolder = temp.newFolder("storage");

    String userHomeDir = homeFolder.getPath();
    config = helper.newFakeConfig().toBuilder()
        .setRootDir(storageFolder.getPath())
        .build();
    storage = new DiskStorage(userHomeDir, config, new Marshaller(helper.console()));
  }

  @Test
  public void testUpdateConfig() throws Exception {
    String newName = "{{new name}}";
    config = config.toBuilder()
        .setCongregationName(newName)
        .build();

    storage.updateConfig(config);

    String contents =
        Files.toString(new File(homeFolder, DiskStorage.CONFIG_FILE_NAME), StandardCharsets.UTF_8);
    assertThat(contents).contains(newName);
  }

  @Test
  public void testSaveAccountsMonth() throws IOException {
    YearMonth date = YearMonth.of(2015, 10);

    AccountsMonth month = helper.newEmptyMonth(date);

    storage.writeMonth(month);

    assertThat(storageFolder.list()).asList().contains("2015-10");
    assertThat(new File(storageFolder, "2015-10").list()).asList()
        .contains(DiskStorage.ACCOUNTS_FILE_NAME);

    AccountsMonth roundTrip = storage.readMonth(date);
    assertThat(roundTrip).isEqualTo(month);
  }
}
