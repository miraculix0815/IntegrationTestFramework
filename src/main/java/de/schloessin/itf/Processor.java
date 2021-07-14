package de.schloessin.itf;

import java.nio.file.Path;

/**
 * A {@link Processor} has to convert data in preparation for
 * an integration test.
 * 
 * <p> In the implementation the method {@link #process(Path)}
 * uses the component under test to convert the source data.
 * The result will be compared with using the
 * {@link FileComparator} later on.
 * 
 * <p> Any failure is to be signaled by an appropriate {@link Exception}.
 * 
 * <p> created 2012-08-25
 * @see FileComparator
 * @see IntegrationTest
 * @author Jan Schlößin
 */
public interface Processor {
  
  /**
   * Processes the data given in the source path and creates the result
   * using the component under test.
   * 
   * <p> The source path can be either a file or a directory depending on the
   * input of the component under test. The output of the component has to be
   * stored in a path. The location is to be chosen by the implementation
   * but has to be returned as {@link Path}. The returned result can be either
   * a file or a directory depending on the used component under test.
   * But it has to be the same type as the expected result used to compare the
   * the processed result in each test case. Because files can't be compared
   * to directories.
   * 
   * <p> The {@link Path} returned by this method will be deleted by
   * {@link IntegrationTest} afterwards if the test was successful.
   * 
   * <p> This step is considered successful if no exception was thrown.
   * 
   * @param   source
   *          the source data to process by the component under test
   * 
   * @return  the location of the processed result of the component under test
   */
  public Path process(Path source);

}
