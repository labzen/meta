package cn.labzen.meta;

import cn.labzen.meta.component.bean.ComponentMeta;
import cn.labzen.meta.configuration.ConfigurationProcessor;
import cn.labzen.meta.environment.EnvironmentCollector;
import cn.labzen.meta.environment.Environments;
import cn.labzen.meta.system.SystemInformation;
import cn.labzen.meta.system.SystemInformationCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class Labzens {

  private static final Logger LOGGER = LoggerFactory.getLogger(Labzens.class);

  private static final Map<String, ComponentMeta> componentMetas = new ConcurrentHashMap<>();

  private Labzens() {
  }

  static void addComponentMeta(@Nonnull ComponentMeta componentMeta) {
    if (componentMeta.information() == null) {
      LOGGER.warn("ComponentMeta 的 information 为 null，已忽略");
      return;
    }

    String title = componentMeta.information().title();
    if (title == null || title.isEmpty()) {
      LOGGER.warn("ComponentMeta 的 title 为空，已忽略");
      return;
    }

    componentMetas.put(componentMeta.information().title(), componentMeta);
  }

  public static Optional<ComponentMeta> getComponentMeta(String title) {
    return Optional.ofNullable(componentMetas.get(title));
  }

  public static Map<String, ComponentMeta> getComponentMetas() {
    return Collections.unmodifiableMap(componentMetas);
  }

  public static <CI> CI configurationWith(Class<CI> clazz) {
    return ConfigurationProcessor.getInterfaceProxy(clazz);
  }

  public static List<SystemInformation> allSystemInformation() {
    return SystemInformationCollector.getAllInformation();
  }

  public static Environments allEnvironments() {
    return EnvironmentCollector.getEnvironments();
  }
}
