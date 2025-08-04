package cn.labzen.meta.environment;

import java.io.File;
import java.nio.file.FileSystems;

import static java.lang.System.getProperty;

public final class EnvironmentCollector {

  private static final Environments ENVIRONMENTS;

  static {
    ENVIRONMENTS = new Environments(getProperty("java.version"),
        getProperty("java.vendor"),
        getProperty("java.vendor.url"),
        getProperty("java.home"),
        getProperty("java.class.version"),
        getProperty("java.class.path"),
        getProperty("java.library.path"),
        File.pathSeparator,
        FileSystems.getDefault().getSeparator(),
        System.lineSeparator(),
        getProperty("user.home"),
        getProperty("user.dir"),
        getProperty("java.io.tmpdir"));
  }

  private EnvironmentCollector() {
  }

  public static Environments getEnvironments() {
    return ENVIRONMENTS;
  }
}
