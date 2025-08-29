package cn.labzen.meta.component;

import cn.labzen.meta.component.bean.Information;
import org.apache.maven.model.Developer;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.List;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

public class Manifest {

  private final DeclaredComponent declaredComponent;

  public Manifest(DeclaredComponent declaredComponent) {
    this.declaredComponent = declaredComponent;
  }

  public Information determine() {
    Class<? extends DeclaredComponent> clazz = declaredComponent.getClass();
    CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();

    Information information;
    information = fromCodeSource(codeSource);
    if (information != null) {
      return information;
    }
    information = fromMaven();
    if (information != null) {
      return information;
    }
    information = fromPackage(clazz.getPackage());
    return information;
  }

  private Information fromCodeSource(CodeSource codeSource) throws RuntimeException {
    try {
      URLConnection connection = codeSource.getLocation().openConnection();
      JarFile jarFile;
      if (connection instanceof JarURLConnection jarConnection) {
        jarFile = jarConnection.getJarFile();
      } else {
        jarFile = new JarFile(new File(codeSource.getLocation().toURI()));
      }

      Attributes attributes;
      try (jarFile) {
        attributes = jarFile.getManifest().getMainAttributes();
        return new Information(attributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE),
            attributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR),
            attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION),
            declaredComponent.description());
      }
    } catch (IOException | URISyntaxException e) {
      return null;
    }
  }

  private Information fromMaven() {
    MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();
    try (FileReader fileReader = new FileReader("pom.xml")) {
      Model model = mavenXpp3Reader.read(fileReader);

      String title = model.getName();
      if (title == null || title.isEmpty()) {
        title = model.getArtifactId();
      }
      String vendor = model.getOrganization().getName();
      if (vendor == null || vendor.isEmpty()) {
        List<Developer> developers = model.getDevelopers();
        if (developers != null && !developers.isEmpty()) {
          vendor = developers.getFirst().getName();
        }
      }
      String version = model.getVersion();
      return new Information(title, vendor, version, declaredComponent.description());
    } catch (IOException | XmlPullParserException e) {
      return null;
    }
  }

  private Information fromPackage(Package pck) {
    String title = Objects.requireNonNullElse(pck.getImplementationTitle(), "");
    String vendor = Objects.requireNonNullElse(pck.getImplementationVendor(), "");
    String version = Objects.requireNonNullElse(pck.getImplementationVersion(), "");
    return new Information(title, vendor, version, declaredComponent.description());
  }
}
