package cn.labzen.meta.configuration.resolver

import cn.labzen.meta.configuration.LabzenConfigurationResolver
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.YamlProcessor
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.yaml.snakeyaml.Yaml
import java.io.InputStream
import java.util.*


class YamlConfigurationResolver : LabzenConfigurationResolver {

  private val logger = LoggerFactory.getLogger(YamlConfigurationResolver::class.java)

  override fun resolve(): Map<String, Any> {
    val pathResolver = PathMatchingResourcePatternResolver()
    val configurationResources = FILE_LOCATIONS.mapNotNull {
      val resource = pathResolver.getResource(it)
      if (resource.exists()) {
        resource
      } else null
    }

    if (configurationResources.isEmpty()) {
      return emptyMap()
    }

    val resource = configurationResources.first()
    if (configurationResources.size > 1) {
      logger.warn("发现多个Labzen配置文件，将默认读取{}，使其生效", resource.filename)
    }

    return loadYamlAsMap(resource.inputStream)
  }

  private fun loadYamlAsMap(inputStream: InputStream): Map<String, Any> {
    val yaml = Yaml()
    val yamlObject = yaml.load<Any>(inputStream) ?: return emptyMap()
    val objectMap = asMap(yamlObject)
    val result = mutableMapOf<String, Any>()
    buildFlattenedMap(result, objectMap, null)
    return result
  }

  /**
   * from spring class: [YamlProcessor]
   */
  private fun asMap(obj: Any): Map<String, Any> {
    // YAML can have numbers as keys
    val result: MutableMap<String, Any> = LinkedHashMap()
    if (obj !is Map<*, *>) {
      // A document can be a text literal
      result["document"] = obj
      return result
    }

    obj.forEach { (key, value) ->
      if (value is Map<*, *>) {
        // It has to be a map key in this case
        result[key.toString()] = asMap(value)
      } else if (key is CharSequence) {
        result[key.toString()] = value as Any
      }
    }
    return result
  }

  private fun buildFlattenedMap(result: MutableMap<String, Any>, source: Map<String, Any?>, path: String?) {
    source.forEach { (key, value) ->
      var k: String = key
      if (path?.isNotBlank() == true) {
        k = "$path.$key"
      }

      if (value is String) {
        result[k] = value
      } else if (value is Map<*, *>) {
        @Suppress("UNCHECKED_CAST")
        buildFlattenedMap(result, value as Map<String, Any>, k)
      } else if (value is Collection<*>) {
        // Need a compound key
        if (value.isEmpty()) {
          result[k] = ""
        } else {
          for ((count, obj) in value.withIndex()) {
            buildFlattenedMap(result, Collections.singletonMap("[$count]", obj), k)
          }
        }
      } else {
        result[k] = value ?: ""
      }
    }
  }


  companion object {
    private val FILE_LOCATIONS = arrayOf(
      "classpath:labzen.yml",
      "classpath:labzen.yaml",
      "classpath:META-INF/labzen.yml",
      "classpath:META-INF/labzen.yaml",
    )
  }
}
