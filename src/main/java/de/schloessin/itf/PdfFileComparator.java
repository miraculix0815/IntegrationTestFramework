package de.schloessin.itf;

import java.io.*;
import java.nio.file.*;
import org.junit.Assert;
import org.pdfbox.cos.COSDocument;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

/** 
 * created 29.08.2012
 * @author jan
 */
public class PdfFileComparator implements FileComparator {

  @Override
  public void assertEquals(Path expectedFile, Path processedFile) {
    try {
      String textOfExpectedPdf = pdfToString(expectedFile)
              .replaceAll("\\s+", " ")
              .toUpperCase();
      String textOfProcessedPdf = pdfToString(processedFile)
              .replaceAll("\\s+", " ")
              .toUpperCase();
        
      Assert.assertEquals(expectedFile.getFileName().toString(),
              textOfExpectedPdf,
              textOfProcessedPdf);     
      
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private static String pdfToString(Path file) throws IOException {   
    if (! Files.isRegularFile(file))
      throw new IllegalArgumentException("File " + file.toString() + " does not exist.");

    try (InputStream istream = Files.newInputStream(file)) {
      PDFParser parser = new PDFParser(istream);
      parser.parse();
      COSDocument document = parser.getDocument();
      String text = new PDFTextStripper().getText(new PDDocument(document));
      document.close();
      return text;
    }
  }

}
