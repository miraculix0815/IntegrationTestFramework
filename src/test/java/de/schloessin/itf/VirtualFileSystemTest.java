package de.schloessin.itf;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * created 2013-11-08
 * @author jan
 */
public class VirtualFileSystemTest {

  @Test
  public void testCreationOfAVirtualFile() throws IOException {
    try (VirtualFileSystem fs = new VirtualFileSystem()) {
      Path testfile = fs.createNewFile("testfile");
      assertTrue(Files.exists(testfile));
      assertTrue(Files.isRegularFile(testfile));
    }
  }

  @Test
  public void testCreationOfAVirtualFileWithContent() throws IOException {
    try (VirtualFileSystem fs = new VirtualFileSystem()) {
      List<String> content = Arrays.asList("test", "content");
      Path testfile = fs.createNewFileWithContent("testfile", content);
      assertTrue(Files.exists(testfile));
      assertTrue(Files.isRegularFile(testfile));
      assertEquals(content, Files.readAllLines(testfile, fs.getCharset()));
    }
  }

  @Test
  public void testCreationOfAVirtualDirectory() throws IOException {
    try (VirtualFileSystem fs = new VirtualFileSystem()) {
      assertTrue(Files.isDirectory(fs.createNewDirectory("testdir")));
      assertTrue(Files.isDirectory(fs.createNewDirectory("/testdir1/testdir2")));
    }
  }

  @Test
  public void testAutoCreationOfParentDir() throws IOException {
    try (VirtualFileSystem fs = new VirtualFileSystem()) {
      fs.createNewDirectory("/testdir1/testdir2");
      Path testfile = fs.createNewFile("/testdir1/testdir2/testfile");
      assertTrue(Files.isRegularFile(testfile));
      assertTrue(Files.isDirectory(testfile.getParent()));
    }
  }

  @Test
  public void testFindAllFilesInTree() throws IOException {
    try (VirtualFileSystem fs = new VirtualFileSystem()) {
      final Set<Path> files = new HashSet<>();
      files.add(fs.createNewDirectory("/dir1a/dir1b"));
      files.add(fs.createNewFile("/dir1a/dir1b/file1"));
      files.add(fs.createNewDirectory("/dir2a/dir2b"));
      files.add(fs.createNewFile("/dir2a/dir2b/file2"));
      files.add(fs.createNewFile("/dir1a/dir1b/file3"));

      Files.walkFileTree(fs.getRootDirectories().iterator().next(), new SimpleFileVisitor<Path>() {

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          assertNotNull(files.remove(file));
          return super.visitFile(file, attrs);
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
          assertNotNull(files.remove(dir));
          return super.postVisitDirectory(dir, exc);
        }

      });

      assertTrue(files.isEmpty());
    }

  }

  @Test
  public void testTruncationWithOpen() throws IOException {
    try (VirtualFileSystem fs = new VirtualFileSystem()) {
      Path testfile = fs.createNewFileWithContent("a", "content");
      List<String> content = Collections.singletonList("new content");
      Files.write(testfile, content, Charset.defaultCharset(), StandardOpenOption.TRUNCATE_EXISTING);
      assertEquals(content, Files.readAllLines(testfile, fs.getCharset()));
    }
  }

}
