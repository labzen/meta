package cn.labzen.meta.configuration

import cn.labzen.meta.configuration.bean.Meta
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import org.springframework.beans.SimpleTypeConverter
import java.lang.reflect.Method

class ConfigurationProxy(
  private val configuredInterface: Class<*>,
  private val namespace: String,
  private val metas: Map<Method, Meta>
) : MethodInterceptor {

  override fun intercept(obj: Any, method: Method, args: Array<out Any>?, proxy: MethodProxy?): Any? {
    if (method.name == "toString") {
      return "The proxy instance of configuration interface: $configuredInterface"
    }

    val meta = metas[method] ?: return null
    val value = ConfigurationProcessor.readConfigurationProperty("$namespace.${meta.path}") ?: meta.defaultValue
    if (value == null && meta.required) {
      throw IllegalStateException("配置项[${meta.path}]不能为空")
    }

    return value?.let {
      converter.convertIfNecessary(value, meta.returnType)
    }
  }

  companion object {
    private val converter = SimpleTypeConverter()
  }
}
