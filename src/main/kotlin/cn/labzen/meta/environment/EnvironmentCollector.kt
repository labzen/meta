package cn.labzen.meta.environment

import cn.labzen.meta.Labzens
import cn.labzen.meta.environment.bean.Environments

object EnvironmentCollector {

  fun collect() {
    val properties = System.getProperties()
    with(properties) {
      val env = Environments(
        getProperty("java.version"),
        getProperty("java.vendor"),
        getProperty("java.vendor.url"),
        getProperty("java.home"),
        getProperty("java.class.version"),
        getProperty("java.class.path"),
        getProperty("java.library.path"),
        getProperty("path.separator"),
        getProperty("file.separator"),
        getProperty("line.separator"),
        getProperty("user.home"),
        getProperty("user.dir"),
        getProperty("java.io.tmpdir")
      )
      Labzens.setEnvironment(env)
    }
  }
}
