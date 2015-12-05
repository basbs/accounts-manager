package net.pryden.accounts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

/**
 * Utility class for constructing instances of {@link Console} for tests.
 */
public final class TestConsoleFactory {
  private final ByteArrayOutputStream outputBuffer;
  private final Console console;

  public static TestConsoleFactory createWithoutExpectedInput() {
    return createWithExpectedInput("");
  }

  public static TestConsoleFactory createWithExpectedInput(String expectedInput) {
    return new TestConsoleFactory(expectedInput);
  }

  private TestConsoleFactory(String expectedInput) {
    InputStream in = new ByteArrayInputStream(expectedInput.getBytes(Charset.defaultCharset()));
    outputBuffer = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(outputBuffer);
    this.console = new Console(in, out);
  }

  public Console console() {
    return console;
  }

  public String getOutput() {
    return new String(outputBuffer.toByteArray(), Charset.defaultCharset());
  }
}
