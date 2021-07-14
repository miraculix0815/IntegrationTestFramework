package de.schloessin.itf;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;

/**
 * created 2010-11-11
 * @author jan
 */
public class VirtualFileSystem extends FileSystem {

  private Path path;
  private final FileSystem fs;
  private Charset charset = Charset.forName("cp1252");

  private static VirtualFileSystem standardTestFileSystem = new VirtualFileSystem();
  
  /**
   * Creates a new virtual file system.
   * 
   * The client is responsible for closing this file system after use.
   */
  public VirtualFileSystem() {
    try {
      this.fs = FileSystems.newFileSystem(URI.create("memory:/" + UUID.randomUUID()), null);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  public Path createNewFile(String path, String ... more) {
    try {
      return Files.createFile(fs.getPath(path, more));
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  public Path createNewDirectory(String path, String ... more) {
    try {
      return Files.createDirectories(fs.getPath(path, more));
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  public Path createNewFileWithContent(String path, String ... content) {
    return createNewFileWithContent(path, Arrays.asList(content));
  }

  public Path createNewFileWithContent(String path, List<String> content) {
    try {
      return Files.write(createNewFile(path), content, charset);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public Charset getCharset() {
    return charset;
  }

  public void setCharset(Charset charset) {
    this.charset = charset;
  }
  
  public Path getRoot() {
    return getRootDirectories().iterator().next();
  }
  

  @Override
  public FileSystemProvider provider() {
    return fs.provider();
  }

  @Override
  public void close() throws IOException {
    fs.close();
  }

  @Override
  public boolean isOpen() {
    return fs.isOpen();
  }

  @Override
  public boolean isReadOnly() {
    return fs.isReadOnly();
  }

  @Override
  public String getSeparator() {
    return fs.getSeparator();
  }

  @Override
  public Iterable<Path> getRootDirectories() {
    return fs.getRootDirectories();
  }

  @Override
  public Iterable<FileStore> getFileStores() {
    return fs.getFileStores();
  }

  @Override
  public Set<String> supportedFileAttributeViews() {
    return fs.supportedFileAttributeViews();
  }

  @Override
  public Path getPath(String first, String... more) {
    return fs.getPath(first, more);
  }

  @Override
  public PathMatcher getPathMatcher(String syntaxAndPattern) {
    return fs.getPathMatcher(syntaxAndPattern);
  }

  @Override
  public UserPrincipalLookupService getUserPrincipalLookupService() {
    return fs.getUserPrincipalLookupService();
  }

  @Override
  public WatchService newWatchService() throws IOException {
    return fs.newWatchService();
  }

}
