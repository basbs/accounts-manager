package net.pryden.accounts.testing;

import net.pryden.accounts.Console;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;

/**
 * Fake implementation of {@link Console} for testing.
 */
public final class FakeConsole extends Console {
  private final ByteArrayOutputStream outputBuffer;
  private final PrintStream out;
  private final ArrayDeque<String> responses;

  public FakeConsole() {
    outputBuffer = new ByteArrayOutputStream();
    out = new PrintStream(outputBuffer);
    responses = new ArrayDeque<>();
  }

  @Override
  public void print(String message) {
    out.print(message);
  }

  @Override
  public String readString(String prompt) {
    print(prompt);
    if (responses.peek() == null) {
      return "";
    }
    return responses.pop();
  }

  public void addExpectedInput(String input) {
    responses.add(input);
  }

  public String getOutput() {
    return new String(outputBuffer.toByteArray(), Charset.defaultCharset());
  }
}
