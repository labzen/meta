package cn.labzen.meta.environment;

import java.io.File;
import java.nio.file.FileSystems;

import static java.lang.System.getProperty;

/**
 * Java运行时环境信息收集器
 * <p>
 * 负责收集和封装Java运行时环境的各种属性信息，
 * 包括Java版本、路径、类路径、系统分隔符、用户目录等。
 * 采用静态初始化方式，在类加载时即收集信息并缓存。
 */
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

  /**
   * 获取Java运行时环境信息
   *
   * @return 包含所有环境信息的不可变对象
   */
  public static Environments getEnvironments() {
    return ENVIRONMENTS;
  }
}
