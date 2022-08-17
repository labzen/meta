package cn.labzen.meta.configuration

import cn.labzen.meta.Labzens
import cn.labzen.meta.configuration.annotation.Configured
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import java.util.*

class ConfigurationReader {

  companion object {

    fun read() {
      readInterfaces()
      readConfigurations()
    }

    private fun readInterfaces() {
      val packages = Labzens.components().values.map {
        it.meta.instance.packageBased()
      }.toTypedArray()

      val reflectionBuilder = ConfigurationBuilder().forPackages(*packages).setScanners(Scanners.TypesAnnotated)
      val reflections = Reflections(reflectionBuilder)

      val namespacedConfigurationInterfaces = reflections.getTypesAnnotatedWith(Configured::class.java)
      namespacedConfigurationInterfaces.forEach(ConfigurationProcessor::parseInterface)
    }

    private fun readConfigurations() {
      val serviceLoader = ServiceLoader.load(LabzenConfigurationResolver::class.java)

      val configurationProperties = mutableMapOf<String, Any?>()
      serviceLoader.map {
        val resolved = it.resolve()
        configurationProperties.putAll(resolved)
      }
      ConfigurationProcessor.saveConfigurationProperties(configurationProperties)
    }
  }
}
