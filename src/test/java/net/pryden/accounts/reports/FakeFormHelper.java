package net.pryden.accounts.reports;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Fake implementation of {@link FormHelper} for testing.
 *
 * <p>Also implements {@link net.pryden.accounts.reports.FormHelper.Factory} for convenience; the
 * factory always returns {@code this} instance.
 */
final class FakeFormHelper extends FormHelper implements FormHelper.Factory {
  private final Map<String, String> formValues = new HashMap<>();
  private final Map<String, Boolean> formCheckboxes = new HashMap<>();

  @Override
  public FormHelper create(String formFilePath, Path outputFilePath) throws IOException {
    return this;
  }

  @Override
  void setValue(String fieldName, String value) {
    formValues.put(fieldName, value);
  }

  Optional<String> getValue(String fieldName) {
    return Optional.ofNullable(formValues.get(fieldName));
  }

  void assertValue(String fieldName, String value) {
    assertThat(formValues).containsEntry(fieldName, value);
  }

  @Override
  void setCheckBox(String fieldName, boolean checked) {
    formCheckboxes.put(fieldName, checked);
  }

  Optional<Boolean> getCheckBox(String fieldName) {
    return Optional.ofNullable(formCheckboxes.get(fieldName));
  }

  void assertChecked(String fieldName) {
    assertThat(formCheckboxes).containsEntry(fieldName, true);
  }

  @Override
  void save() throws IOException {}

  @Override
  public void close() throws IOException {}
}
