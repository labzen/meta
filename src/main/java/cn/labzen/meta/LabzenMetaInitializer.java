package cn.labzen.meta;

import cn.labzen.meta.component.DeclaredComponent;
import cn.labzen.meta.component.Manifest;
import cn.labzen.meta.component.bean.ComponentMeta;
import cn.labzen.meta.component.bean.Information;
import cn.labzen.meta.configuration.ConfigurationProcessor;
import cn.labzen.meta.spring.SpringInitializationOrder;
import cn.labzen.meta.system.SystemInformationCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

import javax.annotation.Nonnull;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * Spring应用上下文初始化器
 * <p>
 * 在Spring容器启动时执行，负责加载组件、收集系统信息和处理配置。
 * 通过ServiceLoader机制发现并注册所有实现了{@link DeclaredComponent}接口的模块。
 *
 * @see ApplicationContextInitializer
 * @see DeclaredComponent
 */
public class LabzenMetaInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

  private final Logger logger = LoggerFactory.getLogger(LabzenMetaInitializer.class);

  @Override
  public void initialize(@Nonnull ConfigurableApplicationContext applicationContext) throws ServiceConfigurationError {
    // 步骤1：加载所有声明的组件
    loadComponents();

    // 步骤2：收集系统硬件信息
    SystemInformationCollector.collect();

    // 步骤3：读取配置文件和组件配置接口
    try {
      ConfigurationProcessor.readConfigurations();
      ConfigurationProcessor.readComponentInterfaces();
    } catch (Exception e) {
      logger.error("读取Labzen配置或配置接口失败，可能会影响应用启动", e);
    }
  }

  /**
   * 加载所有通过ServiceLoader发现的组件
   * <p>
   * 遍历META-INF/services中声明的所有{@link DeclaredComponent}实现类，
   * 提取组件信息并注册到全局缓存中。
   *
   * @throws ServiceConfigurationError 若组件加载失败
   */
  private void loadComponents() throws ServiceConfigurationError {
    ServiceLoader<DeclaredComponent> loaded = ServiceLoader.load(DeclaredComponent.class);
    for (DeclaredComponent component : loaded) {
      try {
        Manifest manifest = new Manifest(component);
        Information information = manifest.determine();

        ComponentMeta componentMeta = new ComponentMeta(information, component);
        assert information != null;
        Labzens.addComponentMeta(componentMeta);
      } catch (Exception e) {
        logger.error("加载Labzen组件 [{}] 失败，跳过", component.getClass().getName(), e);
      }
    }
  }

  @Override
  public int getOrder() {
    return SpringInitializationOrder.MODULE_META_INITIALIZER_ORDER;
  }
}
