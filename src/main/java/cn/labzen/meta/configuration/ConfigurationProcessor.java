package cn.labzen.meta.configuration;

import cn.labzen.meta.Labzens;
import cn.labzen.meta.configuration.annotation.Configured;
import cn.labzen.meta.configuration.annotation.Item;
import cn.labzen.meta.configuration.bean.Meta;
import cn.labzen.meta.configuration.resolver.ConfigurationFileResolver;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class ConfigurationProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationProcessor.class);

  private static final Map<Class<?>, Object> PROXIES = new ConcurrentHashMap<>();

  private ConfigurationProcessor() {
  }

  public static void readConfigurations() throws ServiceConfigurationError {
    ServiceLoader<ConfigurationFileResolver> loaded = ServiceLoader.load(ConfigurationFileResolver.class);

    for (ConfigurationFileResolver resolver : loaded) {
      try {
        Map<String, Object> resolved = resolver.resolve();
        ConfigurationProperties.putAll(resolved);
      } catch (Exception e) {
        LOGGER.error("Labzen配置解析器 [{}] 执行失败，跳过", resolver.getClass().getName(), e);
      }
    }
  }

  public static void readComponentInterfaces() {
    String[] packages = Labzens.getComponentMetas()
                               .values()
                               .stream()
                               .map(cm -> cm.component().packageBased())
                               .toArray(String[]::new);

    // 如果没有组件，packages 为空数组
    if (packages.length == 0) {
      // 无需扫描配置接口
      return;
    }

    ConfigurationBuilder configurationBuilder = new ConfigurationBuilder().forPackages(packages)
                                                                          .setScanners(Scanners.TypesAnnotated);
    Reflections reflections = new Reflections(configurationBuilder);

    Set<Class<?>> configuredInterfaces = reflections.getTypesAnnotatedWith(Configured.class);
    configuredInterfaces.forEach(ConfigurationProcessor::parseInterface);
  }

  @SuppressWarnings("unchecked")
  public static <CI> CI getInterfaceProxy(Class<CI> interfaceClass) {
    if (!PROXIES.containsKey(interfaceClass)) {
      throw new IllegalStateException("未知的配置接口：" + interfaceClass.getName());
    }

    return (CI) PROXIES.get(interfaceClass);
  }

  private static void parseInterface(Class<?> configuredInterface) {
    if (!configuredInterface.isInterface()) {
      throw new IllegalArgumentException("Labzen组件的配置必须为接口");
    }

    Map<Method, Meta> metas = Arrays.stream(configuredInterface.getMethods())
                                    .map(ConfigurationProcessor::parseMethod)
                                    .collect(Collectors.toMap(Meta::method, meta -> meta));
    Configured annotation = configuredInterface.getAnnotation(Configured.class);
    assert annotation != null;
    String namespace = annotation.namespace();
    Object proxy = createConfigurationProxy(configuredInterface, namespace, metas);

    PROXIES.put(configuredInterface, proxy);
  }

  private static Meta parseMethod(Method method) {
    Item annotation = method.getAnnotation(Item.class);

    String path;
    if (annotation != null && !annotation.path().isEmpty()) {
      path = annotation.path();
    } else {
      StringBuilder chips = new StringBuilder();
      char[] nameChars = method.getName().toCharArray();
      for (char c : nameChars) {
        if (Character.isUpperCase(c)) {
          chips.append("-");
        }
        chips.append(Character.toLowerCase(c));
      }
      path = chips.toString();
    }
    boolean required = annotation != null && annotation.required();
    Level loglevel = annotation != null ? annotation.logLevel() : Level.DEBUG;
    String defaultValue = annotation != null ? annotation.defaultValue() : "";
    if (defaultValue.isEmpty()) {
      defaultValue = null;
    }

    return new Meta(method, method.getReturnType(), path, required, loglevel, defaultValue);
  }

  private static Object createConfigurationProxy(Class<?> configuredInterface,
                                                 String namespace,
                                                 Map<Method, Meta> metas) {
    ProxyFactory proxyFactory = new ProxyFactory();
    proxyFactory.setInterfaces(new Class<?>[]{configuredInterface});
    Class<?> createdClass = proxyFactory.createClass();

    try {
      Object proxiedInstance = createdClass.getDeclaredConstructor().newInstance();
      ConfigurationMethodHandler methodHandler = new ConfigurationMethodHandler(configuredInterface, namespace, metas);
      ((ProxyObject) proxiedInstance).setHandler(methodHandler);

      return proxiedInstance;
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("无法创建Labzen配置接口的代理类", e);
    }
  }
}
