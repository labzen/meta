package cn.labzen.meta

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.Ordered

class LabzenComponentMetaInitializer : ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

  override fun getOrder(): Int = 0

  override fun initialize(p0: ConfigurableApplicationContext) {
    Labzens.recording()

    LabzenInformationPrinter.print()
  }
}
