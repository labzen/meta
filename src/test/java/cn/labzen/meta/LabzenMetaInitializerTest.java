package cn.labzen.meta;

import cn.labzen.meta.spring.SpringInitializationOrder;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

class LabzenMetaInitializerTest {

  @Test
  void testGetOrder() {
    LabzenMetaInitializer initializer = new LabzenMetaInitializer();
    
    assertEquals(SpringInitializationOrder.MODULE_META_INITIALIZER_ORDER, initializer.getOrder());
  }

  @Test
  void testInitialize() {
    LabzenMetaInitializer initializer = new LabzenMetaInitializer();
    ConfigurableApplicationContext context = new GenericApplicationContext();
    
    // 初始化不应该抛异常
    assertDoesNotThrow(() -> initializer.initialize(context));
  }

  @Test
  void testInitializeLoadsComponents() {
    LabzenMetaInitializer initializer = new LabzenMetaInitializer();
    ConfigurableApplicationContext context = new GenericApplicationContext();
    
    initializer.initialize(context);
    
    // 验证组件已加载（至少应该有MetaMeta）
    assertNotNull(Labzens.getComponentMetas());
  }

  @Test
  void testInitializeCollectsSystemInformation() {
    LabzenMetaInitializer initializer = new LabzenMetaInitializer();
    ConfigurableApplicationContext context = new GenericApplicationContext();
    
    initializer.initialize(context);
    
    // 验证系统信息已收集
    assertFalse(Labzens.allSystemInformation().isEmpty());
  }

  @Test
  void testInitializeMultipleTimes() {
    LabzenMetaInitializer initializer = new LabzenMetaInitializer();
    ConfigurableApplicationContext context = new GenericApplicationContext();
    
    // 多次初始化不应该抛异常
    assertDoesNotThrow(() -> {
      initializer.initialize(context);
      initializer.initialize(context);
    });
  }
}
