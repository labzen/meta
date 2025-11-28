package cn.labzen.meta.configuration;

import cn.labzen.meta.configuration.annotation.Configured;
import cn.labzen.meta.configuration.annotation.Item;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationProcessorTest {

  @Configured(namespace = "test")
  interface TestConfig {
    @Item(path = "string-value")
    String stringValue();

    @Item(path = "int-value")
    Integer intValue();

    @Item(path = "boolean-value")
    Boolean booleanValue();

    @Item(path = "non-existent", required = false)
    String nonExistent();

    @Item(path = "with-default", required = false, defaultValue = "default-value")
    String withDefault();
  }

  @Configured(namespace = "database")
  interface DatabaseConfig {
    @Item(path = "host")
    String host();

    @Item(path = "port")
    Integer port();
  }

  @BeforeAll
  static void setup() {
    try {
      ConfigurationProcessor.readConfigurations();
    } catch (Exception e) {
      // 忽略，某些环境可能没有配置文件
    }
  }

  @Test
  void testReadConfigurations() {
    assertDoesNotThrow(() -> ConfigurationProcessor.readConfigurations());
  }

  @Test
  void testReadComponentInterfaces() {
    // 这个方法需要组件已经注册
    assertDoesNotThrow(() -> ConfigurationProcessor.readComponentInterfaces());
  }

  @Test
  void testGetInterfaceProxyThrowsForUnknownInterface() {
    @Configured(namespace = "unknown")
    interface UnknownConfig {
      String value();
    }

    assertThrows(IllegalStateException.class, 
        () -> ConfigurationProcessor.getInterfaceProxy(UnknownConfig.class));
  }
}
