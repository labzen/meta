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

/**
 * 配置处理器
 * <p>
 * 负责加载YAML配置文件、解析配置接口、创建动态代理。
 * 使用Javassist库创建配置接口的代理实现，通过{@link ConfigurationMethodHandler}拦截方法调用，
 * 实现从配置文件中读取值并自动类型转换的功能。
 *
 * @see ConfigurationMethodHandler
 * @see ConfigurationFileResolver
 */
public final class ConfigurationProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationProcessor.class);

  /**
   * 配置接口代理实例对象缓存
   */
  private static final Map<Class<?>, Object> PROXIES = new ConcurrentHashMap<>();

  private ConfigurationProcessor() {
  }

  /**
   * 读取并加载所有配置文件
   * <p>
   * 通过ServiceLoader加载所有{@link ConfigurationFileResolver}实现，
   * 执行配置解析并将结果存入全局配置属性Map。
   *
   * @throws ServiceConfigurationError 若配置解析失败
   */
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

  /**
   * 扫描并解析所有组件中的配置接口
   * <p>
   * 根据已加载组件的包路径，使用Reflections库扫描所有标注了@Configured注解的接口，
   * 为每个接口创建动态代理实例并缓存。
   */
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

  /**
   * 获取配置接口的代理实例
   * <p>
   * 从缓存中获取已创建的代理实例，若不存在则抛出异常。
   *
   * @param interfaceClass 配置接口类型
   * @param <CI>           泛型接口类型
   * @return 配置接口的代理实例
   * @throws IllegalStateException 若接口未被正确配置
   */
  @SuppressWarnings("unchecked")
  public static <CI> CI getInterfaceProxy(Class<CI> interfaceClass) {
    if (!PROXIES.containsKey(interfaceClass)) {
      throw new IllegalStateException("未知的配置接口：" + interfaceClass.getName());
    }

    return (CI) PROXIES.get(interfaceClass);
  }

  /**
   * 解析配置接口
   * <p>
   * 提取接口中所有方法的配置元数据，创建代理对象并缓存。
   *
   * @param configuredInterface 配置接口类型
   */
  private static void parseInterface(Class<?> configuredInterface) {
    if (!configuredInterface.isInterface()) {
      throw new IllegalArgumentException("Labzen组件的配置必须为接口");
    }

    Map<Method, Meta> metas = Arrays.stream(configuredInterface.getMethods())
                                    .map(ConfigurationProcessor::parseMethod)
                                    .collect(Collectors.toMap(Meta::method, meta -> meta, (a, b) -> a));
    Configured annotation = configuredInterface.getAnnotation(Configured.class);

    String namespace = annotation.namespace();
    Object proxy = createConfigurationProxy(configuredInterface, namespace, metas);

    PROXIES.put(configuredInterface, proxy);
  }

  /**
   * 解析配置方法
   * <p>
   * 从方法注解和名称中提取配置路径、是否必填、默认值等信息。
   * 配置路径默认从方法名转换而来（驼峰转横线分隔），如getMyValue转为my-value。
   *
   * @param method 配置方法
   * @return 方法的配置元数据
   */
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

  /**
   * 创建配置接口的动态代理实例
   * <p>
   * 使用Javassist的ProxyFactory创建代理类，设置方法处理器拦截所有接口方法的调用。
   *
   * @param configuredInterface 配置接口类型
   * @param namespace           配置命名空间
   * @param metas               方法与配置元数据的映射
   * @return 配置接口的代理实例
   */
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
