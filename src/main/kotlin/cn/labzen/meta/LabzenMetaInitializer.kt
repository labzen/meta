package cn.labzen.meta

import cn.labzen.meta.component.ComponentRecorder
import cn.labzen.meta.configuration.ConfigurationReader
import cn.labzen.meta.environment.EnvironmentCollector
import cn.labzen.meta.spring.SpringApplicationContextInitializerOrder
import cn.labzen.meta.system.SystemInformationCollector
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.Ordered

class LabzenMetaInitializer : ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

  override fun getOrder(): Int =
    SpringApplicationContextInitializerOrder.MODULE_META_INITIALIZER_ORDER

  override fun initialize(p0: ConfigurableApplicationContext) {
    ComponentRecorder.record()
    SystemInformationCollector.collect()
    EnvironmentCollector.collect()
    ConfigurationReader.read()
  }
}
