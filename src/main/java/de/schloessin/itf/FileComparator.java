package de.schloessin.itf;

import java.io.IOException;
import java.nio.file.Path;

/**
 * A {@link FileComparator} tests two regular files for equality.
 * 
 * <p> The definition for equality depends on the test case
 * and file type. For example two text files may be considered equal
 * if the content has the same number of words. Two define an
 * equality for a new test case, this interface has to be implemented.
 * 
 * <p> The equality of directories will be handled by {@link IntegrationTest}
 * directly. Therefor implementing classes have to deal with regular files only.
 * 
 * <p> created 2012-08-25
 * @see TextFileComparator
 * @see IntegrationTest
 * @see Processor
 * @author Jan Schlößin
 */
public interface FileComparator {
  
  /**
   * Test for the equality of the two files given as argument.
   * 
   * <p> This method will be invoked by {@link IntegrationTest} after
   * the {@link Processor} created the processed result to compare it
   * with the expected expected result. This method will be called for
   * every regular file in the result set again.
   * 
   * <p> If the two files have to be seen as unequal, the implementation
   * has to throw an exception meaningful for the tester.
   * A subclass of {@link AssertionError} may be appropriate.
   * 
   * <p> If no exception is thrown the two files are considered equal.
   * 
   * <p> If all files of all directories in the result sets are equal,
   * the test is considered successful and the processed result
   * will be deleted by {@link IntegrationTest}.
   * 
   * @param   expectedFile
   *          a file of the expected result set given by the author
   *          of the test case
   * 
   * @param   processedFile
   *          a file of the processed result set produced by the
   *          component under test in the used {@link Processor}.
   *          This file will be deleted if the whole test has 
   *          ended successful.
   * 
   * @throws  AssertionError
   *          if the result files are unequal according to the definition
   *          of equal for the data
   * 
   * @throws  IOException
   *          if there is a problem reading the data
   */
  public abstract void assertEquals(
          Path expectedFile,
          Path processedFile) throws IOException, AssertionError;
}
