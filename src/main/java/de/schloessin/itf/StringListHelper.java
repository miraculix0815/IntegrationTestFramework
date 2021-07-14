package de.schloessin.itf;

import java.util.*;

/**
 *
 * @author Ralf
 */
public class StringListHelper {

  // Suppresses default constructor, ensuring non-instantiability.

  private StringListHelper() {
  }

  /**
   * Adds any number of {@code List<String>} together. The result is a new
   * ArrayList of Strings.
   *
   * @author Ralf
   * @since 2013-11-20
   * @param lists One or more
   * @return a transitive join over all lists
   */
  public static List<String> joinStrList(List<String>... lists) {
    if (lists.length == 0)
      return Collections.emptyList();
    List<String> strList = new ArrayList<>(lists[0]);
    for (int i = 1; i < lists.length; i ++)
      strList.addAll(lists[i]);
    return strList;
  }

  /**
   * Insert a line feed at the end of each string
   *
   * @author Ralf
   * @since 2013-11-20
   * @param list
   * @return Results a String with line feeds
   */
  public static String strListWithLF(List<String> list) {
    String outStr = "";
    outStr = list.stream().map((string) -> string + "\n").reduce(outStr, String::concat);
    return outStr;
  }

  /**
   * Insert a line number and a line feed at the end of each string
   *
   * @author Ralf
   * @since 2013-11-20
   * @param list
   * @return Results a String with line feeds
   */
  public static String strListWithNumersLF(List<String> list) {
    String outStr = "";
    for (int i = 1; i < list.size(); i ++)
      outStr += String.format("%4d ", i) + list.get(i) + "\n";
    return outStr;
  }
}
