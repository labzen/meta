package cn.labzen.meta.configuration;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局配置属性存储
 * <p>
 * 作为配置属性的中央缓存，存储所有从YAML文件加载的配置项。
 * 使用ConcurrentHashMap保证线程安全，支持并发读写。
 */
final class ConfigurationProperties {

  private static final Map<String, Object> PROPERTIES = new ConcurrentHashMap<>();

  private ConfigurationProperties() {
  }

  static void put(String key, Object value) {
    PROPERTIES.put(key, value);
  }

  static void putAll(Map<String, Object> properties) {
    PROPERTIES.putAll(properties);
  }

  static Object get(String key) {
    return PROPERTIES.get(key);
  }

  static Set<String> keys() {
    return PROPERTIES.keySet();
  }
}
