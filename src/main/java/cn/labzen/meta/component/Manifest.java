package cn.labzen.meta.component;

import cn.labzen.meta.component.bean.Information;
import org.apache.maven.model.Developer;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.*;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.List;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

/**
 * 组件清单读取器
 * <p>
 * 负责从JAR包的Manifest文件、pom.xml或Package元数据中提取组件信息。
 * 按优先级依次尝试：从JAR Manifest读取 → 从pom.xml读取 → 从Package元数据读取。
 */
public class Manifest {

  private static final Logger LOGGER = LoggerFactory.getLogger(Manifest.class);
  private final DeclaredComponent declaredComponent;

  public Manifest(DeclaredComponent declaredComponent) {
    this.declaredComponent = declaredComponent;
  }

  /**
   * 确定组件信息
   * <p>
   * 按优先级尝试获取组件信息：
   * 1. 从JAR的Manifest文件获取（推荐方式）
   * 2. 从项目根目录的pom.xml获取
   * 3. 从Package元数据获取（兜底方式）
   *
   * @return 组件信息对象，若都无法获取则返回null
   */
  public @Nullable Information determine() {
    Class<? extends DeclaredComponent> clazz = declaredComponent.getClass();
    CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();

    Information information;
    if (codeSource != null) {
      information = fromCodeSource(codeSource);
      if (information != null) {
        return information;
      }
    }
    information = fromMaven();
    if (information != null) {
      return information;
    }

    Package pkg = clazz.getPackage();
    if (pkg != null) {
      information = fromPackage(pkg);
    }
    return information;
  }

  /**
   * 从JAR的Manifest文件读取组件信息
   * <p>
   * 读取JAR包中META-INF/MANIFEST.MF文件的实现属性。
   *
   * @param codeSource 类加载来源
   * @return 组件信息，若读取失败则返回null
   */
  private Information fromCodeSource(CodeSource codeSource) throws RuntimeException {
    try {
      URLConnection connection = codeSource.getLocation().openConnection();
      JarFile jarFile;
      if (connection instanceof JarURLConnection jarConnection) {
        jarFile = jarConnection.getJarFile();
      } else {
        jarFile = new JarFile(new File(codeSource.getLocation().toURI()));
      }

      try (jarFile) {
        if (jarFile.getManifest() == null) {
          return null;
        }
        Attributes attributes = jarFile.getManifest().getMainAttributes();
        if (attributes == null) {
          return null;
        }

        String title = attributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
        String vendor = attributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
        String version = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);

        return new Information(title, vendor, version, declaredComponent.description());
      }
    } catch (IOException | URISyntaxException e) {
      return null;
    }
  }

  /**
   * 从当前开发的项目中pom.xml读取组件信息
   * <p>
   * 解析项目根目录下的pom.xml文件，提取项目名称、组织/开发者信息和版本号。
   *
   * @return 组件信息，若文件不存在或解析失败则返回null
   */
  private Information fromMaven() {
    // // 优先从 classpath 读取 Maven POM（生产环境）
    try (InputStream is = getClass().getResourceAsStream("/META-INF/maven/cn.labzen/meta/pom.xml")) {
      if (is != null) {
        try (InputStreamReader reader = new InputStreamReader(is)) {
          return fromInputStream(reader);
        }
      }
    } catch (Exception e) {
      LOGGER.debug("从 classpath 中读取 Maven POM 失败，将尝试从文件系统读取");
    }

    try (FileReader reader = new FileReader("pom.xml")) {
      return fromInputStream(reader);
    } catch (IOException | XmlPullParserException e) {
      LOGGER.debug("从文件系统中读取 Maven POM 失败");
    }

    return null;
  }

  private Information fromInputStream(InputStreamReader reader) throws IOException, XmlPullParserException {
    MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();
    Model model = mavenXpp3Reader.read(reader);

    String title = model.getName();
    if (title == null || title.isEmpty()) {
      title = model.getArtifactId();
    }

    String vendor = null;
    if (model.getOrganization() != null) {
      vendor = model.getOrganization().getName();
    }
    if (vendor == null || vendor.isEmpty()) {
      List<Developer> developers = model.getDevelopers();
      if (developers != null && !developers.isEmpty()) {
        if (developers.getFirst() != null) {
          vendor = developers.getFirst().getName();
        }
      }
    }
    if (vendor == null) {
      vendor = "";
    }

    String version = model.getVersion();
    if (version == null) {
      version = "";
    }

    return new Information(title, vendor, version, declaredComponent.description());
  }

  /**
   * 从Package元数据读取组件信息
   * <p>
   * 作为兜底方案，从类的Package对象获取实现信息。
   *
   * @param pck 类的Package对象
   * @return 组件信息
   */
  private Information fromPackage(Package pck) {
    String title = Objects.requireNonNullElse(pck.getImplementationTitle(), "");
    String vendor = Objects.requireNonNullElse(pck.getImplementationVendor(), "");
    String version = Objects.requireNonNullElse(pck.getImplementationVersion(), "");
    return new Information(title, vendor, version, declaredComponent.description());
  }
}
