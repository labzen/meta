package cn.labzen.meta.configuration;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

final class ConfigurationProperties {

  static final Map<String, Object> PROPERTIES = new ConcurrentHashMap<>();

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
