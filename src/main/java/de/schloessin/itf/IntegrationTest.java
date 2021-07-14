package de.schloessin.itf;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * A {@code IntegrationTest} supports testing of components
 * which processes data and stores it in the file system.
 *
 * <p> A test processes the source data in the first step and compares
 * the processed result with an expected template in the second
 * step.
 *
 * <p> Both steps can be customized via implementation of the
 * interfaces {@link Processor} and  {@link FileComparator}.
 *
 * <p> Once a {@code IntegrationTest} is instantiated with its implementation
 * according to the component under test, it can be used on multiple
 * test cases consisting of pairs of source and result data.
 * A single test is to be executed by invocation of
 * {@link #execute(Path source, Path expectedResult)}.
 * Each test produces temporary results from the source data
 * in the first step which is compared to the expected data in the second step.
 * The source, the processed result and the expected result each can be a file
 * or a directory depending on the component under test.
 *
 * <p> While processing dependents highly on the component under test
 * there are common ways of comparing files and directory trees.
 * Further this class supports the creation of a test by covering the tasks
 * of walking a file tree of result data and its deletion after the test
 * was successful.
 *
 * <p> A test is considered successful if no exception was thrown by all
 * steps of the test.
 *
 * <p> created 2012-08-28
 * @author Jan Schlößin
 * @see FileComparator
 * @see Processor
 */
public class IntegrationTest {
  private final Processor processor;
  private final Processor processor2;
  private final Processor converter;

  private final FileComparator fileComparator;

  public IntegrationTest(Processor processor, FileComparator fileComparator) {
    this.processor = processor;
    this.processor2 = null;
    this.converter = null;
    this.fileComparator = fileComparator;
  }

  public IntegrationTest(Processor processor, Processor processor2, FileComparator fileComparator) {
    this.processor = processor;
    this.fileComparator = fileComparator;
    this.processor2 = processor2;
    this.converter = null;
  }

  public IntegrationTest(Processor processor, Processor converter, Processor processor2, FileComparator fileComparator) {
    this.processor = processor;
    this.fileComparator = fileComparator;
    this.processor2 = processor2;
    this.converter = converter;
  }


  /**
   * Executes the integration test.
   *
   * <p> First method uses the {@link Processor} for the component
   * under test to process data using the {@code source}.
   * Afterwards the method walks the processed directory tree
   * and compares it with the expected result.
   * Two regular files will be compared using {@link FileComparator}.
   *
   * <p> Both parameters can be regular files or directories depending
   * on the component under test. But the type of the expected result have
   * to match the type of the processed result of the used {@link Processor}.
   * Because files can't be compared to directories.
   *
   * @param   source
   *          the data for the component under test to work on
   *
   * @param   expectedResult
   *          the expected result the component under test has to
   *          produce
   *
   * @throws Exception
   *         if either the data can't be processed or the result sets are
   *         unequal
   */
  public void execute(URL source, URL expectedResult) throws Exception {
    Objects.requireNonNull(source, "source must not be null");
    Objects.requireNonNull(expectedResult, "expectedResult must not be null");
    execute(Paths.get(source.toURI()), Paths.get(expectedResult.toURI()));
  }

  private void execute(Path source, Path expectedResult) throws IOException {
    Path processedResult = processor.process(source);
    assertEquals(expectedResult, processedResult);
    deleteDirectory(processedResult);
  }

  public void execute(URL source, URL source2, boolean delResults) throws Exception {
    Objects.requireNonNull(source, "source must not be null");
    Objects.requireNonNull(source2, "source2 must not be null");
    execute(Paths.get(source.toURI()), Paths.get(source2.toURI()), delResults);
  }

  private void execute(Path source, Path source2, boolean delResults) throws IOException {
    Path processedResult = processor.process(source);
    Path processedResult2 = processor2.process(source2);
    assertEquals(processedResult, processedResult2);
    if (delResults){
      deleteDirectory(processedResult);
      deleteDirectory(processedResult2);
    }
  }

  public void execute(URL source) throws Exception {
    Objects.requireNonNull(source, "source must not be null");
    execute(Paths.get(source.toURI()));
  }

  private void execute(Path source) throws IOException {

    Path processedResult = processor.process(source);
    Path convertedResult = converter.process(source);
    Path processConvertedResult = processor2.process(convertedResult);

    assertEquals(processConvertedResult, processedResult);

    deleteDirectory(processedResult);
    deleteDirectory(convertedResult);
    deleteDirectory(processConvertedResult);
  }


  private void assertEquals(Path expectedResult, Path processedResult) throws IOException {
    if (Files.isDirectory(expectedResult) && Files.isDirectory(processedResult))
      assertEqualsDirectories(expectedResult, processedResult);
    else if (Files.isRegularFile(expectedResult) && Files.isRegularFile(processedResult))
      fileComparator.assertEquals(expectedResult, processedResult);
    else if (Files.notExists(expectedResult))
      throw new IllegalArgumentException(expectedResult + " doesn't exist but " + processedResult + " exists.");
    else if (Files.notExists(processedResult))
      throw new IllegalArgumentException(expectedResult + " exists but " + processedResult + " doesn't exist.");
    else
      throw new IllegalArgumentException("the results have to be both directories or both files: " + expectedResult + " " + processedResult);
  }

  private void assertEqualsDirectories(Path expectedResult, Path actualResult) throws IOException {
    Set<Path> processedExpectedFiles = new HashSet<>();

    try (DirectoryStream<Path> actualDirectory = Files.newDirectoryStream(actualResult)) {
      for (Path fileInActualResult : actualDirectory) {
        Path fileInExpectedResult = expectedResult.resolve(fileInActualResult.getFileName());
        processedExpectedFiles.add(fileInExpectedResult);
        assertEquals(fileInExpectedResult, fileInActualResult);
      }
    }

    try (DirectoryStream<Path> expectedDirectory = Files.newDirectoryStream(expectedResult)) {
      for (Path fileInExpectedResult : expectedDirectory) {
        if (! processedExpectedFiles.contains(fileInExpectedResult)) {
          Path fileInActualResult = actualResult.resolve(fileInExpectedResult.getFileName());
          assertEquals(fileInExpectedResult, fileInActualResult);
        }
      }
    }
  }

  private static void deleteDirectory(Path path) throws IOException {
    Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.delete(file);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
        if (e == null) {
          Files.delete(dir);
          return FileVisitResult.CONTINUE;
        } else {
          // directory iteration failed
          throw e;
        }
      }
    });
  }

  public Processor getProcessor() {
    return processor;
  }

  public FileComparator getFileComparator() {
    return fileComparator;
  }

}
