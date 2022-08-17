package cn.labzen.meta.configuration

import cn.labzen.meta.configuration.annotation.Configured
import cn.labzen.meta.configuration.annotation.Item
import cn.labzen.meta.configuration.bean.Meta
import net.sf.cglib.proxy.Enhancer
import org.slf4j.event.Level
import java.lang.reflect.Method

internal object ConfigurationProcessor {

  private val configurationProxies = mutableMapOf<Class<*>, Any>()
  private lateinit var configurationProperties: Map<String, Any?>

  fun parseInterface(configuredInterface: Class<*>) {
    if (!configuredInterface.isInterface) {
      throw IllegalArgumentException("Labzen Configuration配置必须为接口")
    }

    val configurationItemMetas = configuredInterface.methods.map(this::parseMethod).associateBy(Meta::method)
    val namespace = configuredInterface.getAnnotation(Configured::class.java).namespace
    configurationProxies[configuredInterface] = createConfigurationProxy(
      configuredInterface, namespace, configurationItemMetas
    )
  }

  private fun parseMethod(method: Method): Meta {
    val annotation: Item? = method.getAnnotation(Item::class.java)

    val path = if (annotation?.path?.isNotBlank() == true) {
      annotation.path
    } else {
      val result = StringBuilder()
      method.name.forEach { c ->
        result.append(
          if (c.isUpperCase()) {
            "-" + c.lowercaseChar()
          } else c
        )
      }
      result.toString()
    }
    val required = annotation?.required ?: false
    val logLevel = annotation?.logLevel ?: Level.DEBUG
    val defaultValue = if (annotation?.defaultValue?.isNotBlank() == true) annotation.defaultValue else null

    return Meta(method, method.returnType, path, required, logLevel, defaultValue)
  }

  private fun createConfigurationProxy(
    configuredInterface: Class<*>,
    namespace: String,
    configurationItemMetas: Map<Method, Meta>
  ): Any {
    val enhancer = Enhancer()
    enhancer.setInterfaces(arrayOf(configuredInterface))
    enhancer.setCallback(ConfigurationProxy(configuredInterface, namespace, configurationItemMetas))
    return enhancer.create()
  }

  fun <CI> getInterfaceProxy(inter: Class<CI>): CI {
    if (!configurationProxies.containsKey(inter)) {
      throw IllegalArgumentException("未知的配置接口：$inter")
    }

    @Suppress("UNCHECKED_CAST")
    return configurationProxies[inter] as CI
  }

  fun saveConfigurationProperties(properties: Map<String, Any?>) {
    configurationProperties = properties
  }

  fun readConfigurationProperty(key: String): Any? =
    configurationProperties[key]

}
