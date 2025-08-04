package cn.labzen.meta.component;

import cn.labzen.meta.component.bean.Information;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.security.CodeSource;
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

    try {
      return fromCodeSource(codeSource);
    } catch (RuntimeException e) {
      return fromPackage(clazz.getPackage());
    }
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
      throw new RuntimeException(e);
    }
  }

  private Information fromPackage(Package pck) {
    String title = Objects.requireNonNullElse(pck.getImplementationTitle(), "");
    String vendor = Objects.requireNonNullElse(pck.getImplementationVendor(), "");
    String version = Objects.requireNonNullElse(pck.getImplementationVersion(), "");
    return new Information(title, vendor, version, declaredComponent.description());
  }
}
