package de.schloessin.itf;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import junit.framework.Assert;

/**
 * created 29.08.2012
 * @author jan
 */
public class TextFileComparator implements FileComparator {
  
  private final Charset charset;
  private final List<Pattern> linesToIgnore = new ArrayList<>();
  private boolean trimLiniesBeforeCompare = false;

  public TextFileComparator() {
    this(Charset.defaultCharset());
  }

  public TextFileComparator(Charset charset) {
    this.charset = charset;
  }
  
  public TextFileComparator ignoreLiniesWhichAreMatchedBy(Pattern toIgnore) {
    linesToIgnore.add(toIgnore);
    return this;
  }

  public TextFileComparator trimEachLinieBeforeCompare() {
    trimLiniesBeforeCompare = true;
    return this;
  }

  @Override
  public void assertEquals(Path expectedFile, Path actualFile) throws IOException, AssertionError {
    try (
            LineNumberReader expected = new LineNumberReader(Files.newBufferedReader(expectedFile, charset));
            LineNumberReader actual = new LineNumberReader(Files.newBufferedReader(actualFile, charset));
            ) {
      String expectedLine = getNextLineToTakenCareOf(expected);
      String actualLine = getNextLineToTakenCareOf(actual);
      while (expectedLine != null || actualLine != null) {
        if (expectedLine != null && actualLine != null) {
          if (trimLiniesBeforeCompare) {
            expectedLine = expectedLine.trim();
            actualLine = actualLine.trim();
          }
        }
          
        Assert.assertEquals(
                "File " + expectedFile + ":" + expected.getLineNumber()
                + " differs from " + actualFile + ":" + actual.getLineNumber(),
                expectedLine,
                actualLine);
        
        expectedLine = getNextLineToTakenCareOf(expected);
        actualLine = getNextLineToTakenCareOf(actual);
      }
    }
  }
  
  private String getNextLineToTakenCareOf(LineNumberReader processed) throws IOException {
    String line;
    
    do line = processed.readLine();
    while (line != null && ! isToBeTakenCareOf(line));
    
    return line;
  }

  private boolean isToBeTakenCareOf(String line) {
    for (Pattern p : linesToIgnore)
      if (p.matcher(line).matches())
        return false;
    
    return true;
  }

}
