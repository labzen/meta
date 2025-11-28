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

public class LabzenMetaInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

  private final Logger logger = LoggerFactory.getLogger(LabzenMetaInitializer.class);

  @Override
  public void initialize(@Nonnull ConfigurableApplicationContext applicationContext) throws ServiceConfigurationError {
    loadComponents();

    SystemInformationCollector.collect();

    try {
      ConfigurationProcessor.readConfigurations();
      ConfigurationProcessor.readComponentInterfaces();
    } catch (Exception e) {
      logger.error("读取Labzen配置或配置接口失败，可能会影响应用启动", e);
    }
  }

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
