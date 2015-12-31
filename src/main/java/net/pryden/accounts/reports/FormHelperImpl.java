package net.pryden.accounts.reports;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckbox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Concrete implementation of {@link FormHelper}. Basically wraps the PDFBox API.
 */
final class FormHelperImpl extends FormHelper {
  private final String formFilePath;
  private final PDDocument document;
  private final PDAcroForm form;
  private final File outputFile;

  static final FormHelper.Factory FACTORY = new Factory();

  /** Implementation of {@link FormHelper.Factory}. */
  private static final class Factory implements FormHelper.Factory {
    @Override
    public FormHelper create(String formFilePath, Path outputFilePath) throws IOException {
      File formFile = new File(formFilePath);
      if (!formFile.canRead()) {
        throw new IllegalArgumentException("Input file " + formFilePath + " cannot be read.");
      }
      File outputFile = outputFilePath.toFile();
      if (outputFile.exists() && !outputFile.canWrite()) {
        throw new IllegalArgumentException("Output file " + outputFilePath + " cannot be written.");
      }
      PDDocument document = PDDocument.load(formFile);
      PDAcroForm form = document.getDocumentCatalog().getAcroForm();
      if (form == null) {
        document.close();
        throw new IllegalStateException("PDF file " + formFilePath + " is not a PDF form.");
      }
      return new FormHelperImpl(formFilePath, document, form, outputFile);
    }
  }

  FormHelperImpl(String formFilePath, PDDocument document, PDAcroForm form, File outputFile) {
    this.formFilePath = formFilePath;
    this.document = document;
    this.form = form;
    this.outputFile = outputFile;
  }

  private <T extends PDField> T getField(String fieldName, Class<T> fieldType) {
    PDField field = form.getField(fieldName);
    if (field == null) {
      throw new IllegalArgumentException(
          String.format("No such field %s in form %s", fieldName, formFilePath));
    }
    return fieldType.cast(field);
  }

  @Override
  public void setValue(String fieldName, String value) throws IOException {
    getField(fieldName, PDTextField.class).setValue(value);
  }

  @Override
  public void setCheckBox(String fieldName, boolean checked) throws IOException {
    PDCheckbox checkbox = getField(fieldName, PDCheckbox.class);
    if (checked) {
      checkbox.check();
    } else {
      checkbox.unCheck();
    }
  }

  @Override
  public void save() throws IOException {
    if (outputFile.exists()) {
      File backupFile = new File(outputFile.getPath() + ".bak");
      if (backupFile.exists()) {
        backupFile.delete();
      }
      outputFile.renameTo(backupFile);
    }
    document.save(outputFile);
  }

  @Override
  public void close() throws IOException {
    document.close();
  }
}
