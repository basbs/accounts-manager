package net.pryden.accounts.reports;

import net.pryden.accounts.model.Money;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Helper API for working with PDF forms. This interface was extracted from its implementation
 * {@link FormHelperImpl} to allow for a fake implementation used in tests.
 */
abstract class FormHelper implements AutoCloseable {
  /**
   * Factory for {@link FormHelper} instances.
   */
  interface Factory {
    /**
     * Constructs a new {@link FormHelper} instance.
     *
     * @param formFilePath the path to the empty PDF form to use as a template
     * @param outputFilePath the path where the completed PDF form should be written
     * @return a new {@code FormHelper} instance
     * @throws IOException if the PDF form cannot be read
     */
    FormHelper create(String formFilePath, Path outputFilePath) throws IOException;
  }


  /**
   * Sets the value of the form field named by {@code fieldName} to the given string {@code value}.
   */
  abstract void setValue(String fieldName, String value) throws IOException;

  /**
   * Sets the value of the form field named by {@code fieldName} to a formatted representation of
   * the given {@link Money} {@code value}.
   */
  public void setMoney(String fieldName, Money value) throws IOException {
    setValue(fieldName, value.toFormattedString());
  }

  /**
   * Sets the value of the form field named by {@code fieldName} to a formatted representation of
   * the given {@link Money} {@code value}. If the currency value is zero, this method will output
   * a literal "0.00" rather than an empty string.
   */
  public void setMoneyPreserveZero(String fieldName, Money value) throws IOException {
    setValue(fieldName, value.toFormattedStringPreserveZero());
  }

  /**
   * Sets the checked state of the form checkbox field named by {@code fieldName} to the given
   * {@code checked} state.
   */
  abstract void setCheckBox(String fieldName, boolean checked) throws IOException;

  /** Writes the form field out to disk. */
  abstract void save() throws IOException;

  /** Closes the form and cleans up resources. */
  @Override
  public abstract void close() throws IOException;
}
