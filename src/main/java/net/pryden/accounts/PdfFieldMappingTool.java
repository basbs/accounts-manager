package net.pryden.accounts;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.File;
import java.io.IOException;

/**
 * Simple tool for determining which PDF field names map to which fields. It is intended to be
 * invoked from the command line.
 */
final class PdfFieldMappingTool {
  public static void main(String[] args) throws IOException {
    if (args.length < 2) {
      throw new IllegalArgumentException(
          "Usage: PdfFieldMappingTool <inputFile> <outputFile>");
    }
    String inputFileName = args[0];
    String outputFileName = args[1];

    File inputFile = new File(inputFileName);
    if (!inputFile.canRead()) {
      throw new IllegalArgumentException("Input file " + inputFileName + " cannot be read.");
    }
    File outputFile = new File(outputFileName);
    if (outputFile.exists() && !outputFile.canWrite()) {
      throw new IllegalArgumentException("Output file " + outputFileName + " cannot be written.");
    }

    System.out.printf("Opening input file %s...\n", inputFileName);
    try (PDDocument document = PDDocument.load(inputFile)) {
      PDAcroForm form = document.getDocumentCatalog().getAcroForm();
      if (form == null) {
        throw new IllegalStateException("PDF file is not a PDF form");
      }

      for (PDField field : form.getFieldTree()) {
        if (!field.isReadOnly()) {
          field.setValue(field.getFullyQualifiedName());
        }
        System.out.printf("Field %s\n", field.getFullyQualifiedName());
      }

      System.out.printf("Writing output file %s...\n", outputFileName);
      document.save(outputFile);
    }
    System.out.println("Done!");
  }
}
