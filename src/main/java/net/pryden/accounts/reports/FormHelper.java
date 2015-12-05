package net.pryden.accounts.reports;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckbox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;

/**
 * Helper class for working with PDF forms. Basically wraps the PDFBox API.
 */
final class FormHelper implements AutoCloseable {
  private final String formFilePath;
  private final PDDocument document;
  private final PDAcroForm form;
  private final File outputFile;

  static FormHelper create(String formFilePath, Path outputFilePath) throws IOException {
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
    return new FormHelper(formFilePath, document, form, outputFile);
  }

  private FormHelper(String formFilePath, PDDocument document, PDAcroForm form, File outputFile) {
    this.formFilePath = formFilePath;
    this.document = document;
    this.form = form;
    this.outputFile = outputFile;
  }

  private PDField getField(String fieldName) {
    PDField field = form.getField(fieldName);
    if (field == null) {
      throw new IllegalArgumentException(
          String.format("No such field %s in form %s", fieldName, formFilePath));
    }
    return field;
  }

  void setValue(String fieldName, String value) throws IOException {
    ((PDTextField) getField(fieldName)).setValue(value);
  }

  void setMoney(String fieldName, BigDecimal value) throws IOException {
    setValue(fieldName, formatMoney(value));
  }

  void setMoneyPreserveZero(String fieldName, BigDecimal value) throws IOException {
    setValue(fieldName, formatMoneyPreserveZero(value));
  }

  void setCheckBox(String fieldName, boolean checked) throws IOException {
    PDCheckbox checkbox = ((PDCheckbox) getField(fieldName));
    if (checked) {
      checkbox.check();
    } else {
      checkbox.unCheck();
    }
  }


  static String formatMoney(BigDecimal value) {
    if (value.equals(BigDecimal.ZERO)) {
      return "";
    }
    return formatMoneyPreserveZero(value);
  }

  static String formatMoneyPreserveZero(BigDecimal value) {
    return value.setScale(2, BigDecimal.ROUND_HALF_EVEN).toPlainString();
  }

  void save() throws IOException {
    document.save(outputFile);
  }

  @Override
  public void close() throws IOException {
    document.close();
  }
}
