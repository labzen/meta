package cn.labzen.meta.spring;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpringInitializationOrderTest {

  @Test
  void testModuleMetaInitializerOrder() {
    assertEquals(Integer.MIN_VALUE + 1_000, SpringInitializationOrder.MODULE_META_INITIALIZER_ORDER);
  }

  @Test
  void testModuleLoggerInitializerOrder() {
    assertEquals(Integer.MIN_VALUE + 2_000, SpringInitializationOrder.MODULE_LOGGER_INITIALIZER_ORDER);
  }

  @Test
  void testModuleSpringInitializerOrder() {
    assertEquals(Integer.MIN_VALUE + 3_000, SpringInitializationOrder.MODULE_SPRING_INITIALIZER_ORDER);
  }

  @Test
  void testModuleCoreInitializerOrder() {
    assertEquals(Integer.MIN_VALUE + 4_000, SpringInitializationOrder.MODULE_CORE_INITIALIZER_ORDER);
  }

  @Test
  void testModulePluginInitializerOrder() {
    assertEquals(Integer.MIN_VALUE + 5_000, SpringInitializationOrder.MODULE_PLUGIN_INITIALIZER_ORDER);
  }

  @Test
  void testModuleSqlInitializerOrder() {
    assertEquals(Integer.MIN_VALUE + 6_000, SpringInitializationOrder.MODULE_SQL_INITIALIZER_ORDER);
  }

  @Test
  void testModuleWebInitializerOrder() {
    assertEquals(Integer.MIN_VALUE + 7_000, SpringInitializationOrder.MODULE_WEB_INITIALIZER_ORDER);
  }

  @Test
  void testModuleAuthorityInitializerOrder() {
    assertEquals(Integer.MIN_VALUE + 8_000, SpringInitializationOrder.MODULE_AUTHORITY_INITIALIZER_ORDER);
  }

  @Test
  void testModuleCacheInitializerOrder() {
    assertEquals(Integer.MIN_VALUE + 9_000, SpringInitializationOrder.MODULE_CACHE_INITIALIZER_ORDER);
  }

  @Test
  void testModuleMqInitializerOrder() {
    assertEquals(Integer.MIN_VALUE + 10_000, SpringInitializationOrder.MODULE_MQ_INITIALIZER_ORDER);
  }

  @Test
  void testModuleRightsInitializerOrder() {
    assertEquals(Integer.MIN_VALUE + 11_000, SpringInitializationOrder.MODULE_RIGHTS_INITIALIZER_ORDER);
  }

  @Test
  void testModuleJavafxInitializerOrder() {
    assertEquals(Integer.MIN_VALUE + 12_000, SpringInitializationOrder.MODULE_JAVAFX_INITIALIZER_ORDER);
  }

  @Test
  void testModuleSwingInitializerOrder() {
    assertEquals(Integer.MIN_VALUE + 12_000, SpringInitializationOrder.MODULE_SWING_INITIALIZER_ORDER);
  }

  @Test
  void testOrderSequence() {
    // 验证顺序是递增的
    assertTrue(SpringInitializationOrder.MODULE_META_INITIALIZER_ORDER 
        < SpringInitializationOrder.MODULE_LOGGER_INITIALIZER_ORDER);
    assertTrue(SpringInitializationOrder.MODULE_LOGGER_INITIALIZER_ORDER 
        < SpringInitializationOrder.MODULE_SPRING_INITIALIZER_ORDER);
    assertTrue(SpringInitializationOrder.MODULE_SPRING_INITIALIZER_ORDER 
        < SpringInitializationOrder.MODULE_CORE_INITIALIZER_ORDER);
  }

  @Test
  void testJavafxAndSwingSameOrder() {
    // JavaFX 和 Swing 应该有相同的顺序
    assertEquals(SpringInitializationOrder.MODULE_JAVAFX_INITIALIZER_ORDER,
        SpringInitializationOrder.MODULE_SWING_INITIALIZER_ORDER);
  }
}
