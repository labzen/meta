package cn.labzen.meta;

import cn.labzen.meta.component.DeclaredComponent;
import cn.labzen.meta.component.Manifest;
import cn.labzen.meta.component.bean.ComponentMeta;
import cn.labzen.meta.component.bean.Information;
import cn.labzen.meta.configuration.ConfigurationProcessor;
import cn.labzen.meta.spring.SpringInitializationOrder;
import cn.labzen.meta.system.SystemInformationCollector;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

import javax.annotation.Nonnull;
import java.util.ServiceLoader;

public class LabzenMetaInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

  @Override
  public void initialize(@Nonnull ConfigurableApplicationContext applicationContext) {
    loadComponents();

    SystemInformationCollector.collect();

    ConfigurationProcessor.readConfigurations();
    ConfigurationProcessor.readComponentInterfaces();
  }

  private void loadComponents() {
    ServiceLoader<DeclaredComponent> loaded = ServiceLoader.load(DeclaredComponent.class);
    for (DeclaredComponent component : loaded) {
      Manifest manifest = new Manifest(component);
      Information information = manifest.determine();

      ComponentMeta componentMeta = new ComponentMeta(information, component);
      Labzens.addComponentMeta(componentMeta);
    }
  }

  @Override
  public int getOrder() {
    return SpringInitializationOrder.MODULE_META_INITIALIZER_ORDER;
  }
}
