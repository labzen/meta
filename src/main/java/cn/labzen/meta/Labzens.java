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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Labzen模块元数据的对外入口类
 * <p>
 * 提供组件元数据获取、配置代理创建、系统信息查询等核心功能。
 * 采用单例模式管理组件元数据，通过动态代理实现类型安全的配置访问。
 */
public final class Labzens {

  private static final Logger LOGGER = LoggerFactory.getLogger(Labzens.class);

  // 使用ConcurrentHashMap保证线程安全，支持并发读写
  private static final Map<String, ComponentMeta> componentMetas = new ConcurrentHashMap<>();

  private Labzens() {
  }

  /**
   * 添加组件元数据到全局缓存
   * <p>
   * 通过ServiceLoader机制加载的组件会被注册到此缓存中，
   * 以组件标题作为唯一标识键。若标题为空或重复则忽略添加。
   *
   * @param componentMeta 组件元数据对象
   */
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

    ComponentMeta existing = componentMetas.putIfAbsent(title, componentMeta);
    if (existing != null) {
      LOGGER.warn("检测到重复的组件标题: {}，已忽略后续注册", title);
    }
  }

  /**
   * 根据组件标题获取组件元数据
   *
   * @param title 组件标题，唯一标识
   * @return 组件元数据，若不存在则返回空Optional
   */
  public static Optional<ComponentMeta> getComponentMeta(String title) {
    return Optional.ofNullable(componentMetas.get(title));
  }

  /**
   * 获取所有已注册的组件元数据
   *
   * @return 不可变的组件元数据Map，键为组件标题
   */
  public static Map<String, ComponentMeta> getComponentMetas() {
    return Map.copyOf(componentMetas);
  }

  /**
   * 获取配置接口的动态代理实例
   * <p>
   * 通过Javassist动态代理创建配置接口的实现，
   * 代理方法调用会从YAML配置文件中读取对应值。
   *
   * @param clazz 配置接口类型
   * @param <CI>  泛型接口类型
   * @return 配置接口的代理实例
   * @throws IllegalStateException 若接口未被正确配置
   */
  public static <CI> CI configurationWith(Class<CI> clazz) {
    return ConfigurationProcessor.getInterfaceProxy(clazz);
  }

  /**
   * 获取所有收集到的系统硬件信息
   *
   * @return 系统信息列表，包含CPU、内存、磁盘、网络等详细信息
   */
  public static List<SystemInformation> allSystemInformation() {
    return SystemInformationCollector.getAllInformation();
  }

  /**
   * 获取Java运行时环境信息
   *
   * @return 包含Java版本、路径、用户目录等环境信息的对象
   */
  public static Environments allEnvironments() {
    return EnvironmentCollector.getEnvironments();
  }
}
