package net.pryden.accounts.model;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class MoneyTest {
  @Test
  public void testParsing() {
    assertParsesAs("0", "0.00");
    assertParsesAs("(17)", "-17.00");
    assertParsesAs("-17", "(17.00)");
    assertParsesAs("1.001", "1.00");
  }

  @Test
  public void testInvariants() {
    assertInvariants("1.00");
    assertInvariants("0.01");
    assertInvariants("0.00");
    assertInvariants("(1.00)");
    assertInvariants("12345678901234567890.12");
  }

  private void assertInvariants(String amount) {
    assertThat(Money.parse(amount)).isEqualTo(Money.parse(amount));
    assertThat(Money.parse(amount).toFormattedStringPreserveZero()).isEqualTo(amount);
    assertThat(Money.parse(amount).plus(Money.parse("0.01"))).isNotEqualTo(Money.parse(amount));
    assertThat(Money.parse(amount).minus(Money.parse("0.01"))).isNotEqualTo(Money.parse(amount));
  }

  private void assertParsesAs(String toParse, String toCompareTo) {
    assertThat(Money.parse(toParse)).isEqualTo(Money.parse(toCompareTo));
  }
}
