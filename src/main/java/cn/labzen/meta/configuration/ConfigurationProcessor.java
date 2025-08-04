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
import org.slf4j.event.Level;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverterSupport;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public final class ConfigurationProcessor {

  private static final TypeConverterSupport CONVERTER = new SimpleTypeConverter();
  private static final Map<String, Object> PROPERTIES = new HashMap<>();
  private static final Map<Class<?>, Object> PROXIES = new HashMap<>();

  private ConfigurationProcessor() {
  }

  public static void readConfigurations() {
    ServiceLoader<ConfigurationFileResolver> loaded = ServiceLoader.load(ConfigurationFileResolver.class);

    for (ConfigurationFileResolver resolver : loaded) {
      Map<String, Object> resolved = resolver.resolve();
      PROPERTIES.putAll(resolved);
    }
  }

  public static void readComponentInterfaces() {
    String[] packages = Labzens.getComponentMetas()
                               .values()
                               .stream()
                               .map(cm -> cm.component().packageBased())
                               .toArray(String[]::new);

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
        chips.append(c);
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
      ((ProxyObject) proxiedInstance).setHandler((self, thisMethod, proceed, args) -> {
        if ("toString".equals(thisMethod.getName())) {
          return "The proxy instance of configuration interface: " + configuredInterface.getName();
        }

        Meta meta = metas.get(thisMethod);
        if (meta == null) {
          throw new IllegalStateException("无法解析的配置类选项方法：" + thisMethod.getName());
        }

        String path = namespace + "." + meta.path();
        Object value = PROPERTIES.get(path);
        if (value == null && meta.required()) {
          throw new IllegalStateException("配置项[" + path + "]不能为空");
        }

        if (value != null) {
          return CONVERTER.convertIfNecessary(value, meta.returnType());
        }
        return null;
      });

      return proxiedInstance;
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("无法创建Labzen配置接口的代理类", e);
    }
  }
}
