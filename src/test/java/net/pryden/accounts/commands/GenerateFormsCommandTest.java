package net.pryden.accounts.commands;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableSet;
import net.pryden.accounts.model.AccountsMonth;
import net.pryden.accounts.reports.Report;
import net.pryden.accounts.testing.TestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

@RunWith(JUnit4.class)
public final class GenerateFormsCommandTest {
  private TestHelper helper = TestHelper.create();

  @Test
  public void testGenerateFormsCommand() throws Exception {
    helper.writeEmptyMonth();
    ImmutableSet<Report> reports = ImmutableSet.of(
        new FakeReport(false),
        new FakeReport(true),
        new FakeReport(false),
        new FakeReport(true),
        new FakeReport(false));
    GenerateFormsCommand command =
        new GenerateFormsCommand(helper.storage(), helper.currentMonth(), reports);
    AccountsMonth month = helper.readCurrentMonth();

    command.run();

    reports.stream()
        .map(r -> (FakeReport) r)
        .forEach(report -> {
          assertThat(report.wasGenerated).named("wasGenerated").isEqualTo(report.applicable);
          assertThat(report.month).isSameAs(month);
        });
  }

  private static final class FakeReport implements Report {
    final boolean applicable;
    AccountsMonth month;
    boolean wasGenerated;

    FakeReport(boolean applicable) {
      this.applicable = applicable;
    }

    @Override
    public boolean isApplicableFor(AccountsMonth month) {
      this.month = month;
      return applicable;
    }

    @Override
    public void generate(AccountsMonth month) throws IOException {
      wasGenerated = true;
      assertThat(month).isSameAs(this.month);
    }
  }
}
