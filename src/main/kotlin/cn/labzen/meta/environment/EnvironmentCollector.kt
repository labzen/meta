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
        getProperty("java.vendor.version"),
        getProperty("java.home"),
        getProperty("java.class.version"),
        getProperty("java.class.path"),
        getProperty("java.library.path"),
        getProperty("line.separator"),
        getProperty("user.timezone"),
        getProperty("user.home"),
        getProperty("user.dir"),
        getProperty("user.language"),
        getProperty("java.io.tmpdir")
      )
      Labzens.setEnvironment(env)
    }
  }
}
