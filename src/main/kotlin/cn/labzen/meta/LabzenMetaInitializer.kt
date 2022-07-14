package cn.labzen.meta

import cn.labzen.meta.component.ComponentRecorder
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.Ordered

class LabzenMetaInitializer : ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

  override fun getOrder(): Int = 0

  override fun initialize(p0: ConfigurableApplicationContext) {
    ComponentRecorder.record()

    LabzenMetaPrinter.print()
  }
}
