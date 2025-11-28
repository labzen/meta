package cn.labzen.meta;

import cn.labzen.meta.component.DeclaredComponent;
import cn.labzen.meta.component.bean.ComponentMeta;
import cn.labzen.meta.component.bean.Information;
import cn.labzen.meta.environment.Environments;
import cn.labzen.meta.system.SystemInformation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LabzensTest {

  private static class TestComponent implements DeclaredComponent {
    @Override
    public String mark() {
      return "test-mark";
    }

    @Override
    public String packageBased() {
      return "cn.labzen.test";
    }

    @Override
    public String description() {
      return "Test component for testing";
    }
  }

  @BeforeAll
  static void setup() {
    // 添加测试组件
    Information info = new Information("Test Component", "Test Vendor", "1.0.0", "Test");
    ComponentMeta meta = new ComponentMeta(info, new TestComponent());
    Labzens.addComponentMeta(meta);
  }

  @Test
  void testGetComponentMeta() {
    Optional<ComponentMeta> meta = Labzens.getComponentMeta("Test Component");
    
    assertTrue(meta.isPresent());
    assertEquals("Test Component", meta.get().information().title());
  }

  @Test
  void testGetComponentMetaNotFound() {
    Optional<ComponentMeta> meta = Labzens.getComponentMeta("NonExistent");
    
    assertFalse(meta.isPresent());
  }

  @Test
  void testGetComponentMetas() {
    Map<String, ComponentMeta> metas = Labzens.getComponentMetas();
    
    assertNotNull(metas);
    assertFalse(metas.isEmpty());
    assertTrue(metas.containsKey("Test Component"));
  }

  @Test
  void testGetComponentMetasUnmodifiable() {
    Map<String, ComponentMeta> metas = Labzens.getComponentMetas();
    
    Information info = new Information("Another", "Vendor", "1.0", "Desc");
    ComponentMeta newMeta = new ComponentMeta(info, new TestComponent());
    
    assertThrows(UnsupportedOperationException.class, 
        () -> metas.put("Another", newMeta));
  }

  @Test
  void testAddComponentMetaWithNullInformation() {
    ComponentMeta metaWithNullInfo = new ComponentMeta(null, new TestComponent());
    
    // 不应该抛异常，但会被忽略
    assertDoesNotThrow(() -> Labzens.addComponentMeta(metaWithNullInfo));
  }

  @Test
  void testAddComponentMetaWithEmptyTitle() {
    Information emptyTitleInfo = new Information("", "Vendor", "1.0", "Desc");
    ComponentMeta meta = new ComponentMeta(emptyTitleInfo, new TestComponent());
    
    // 不应该抛异常，但会被忽略
    assertDoesNotThrow(() -> Labzens.addComponentMeta(meta));
  }

  @Test
  void testAddComponentMetaWithNullTitle() {
    // Record可以接受null值，尽管标记了@Nonnull
    Information nullTitleInfo = new Information(null, "Vendor", "1.0", "Desc");
    ComponentMeta meta = new ComponentMeta(nullTitleInfo, new TestComponent());
    
    // 不应该抛异常，但会被忽略
    assertDoesNotThrow(() -> Labzens.addComponentMeta(meta));
  }

  @Test
  void testAllSystemInformation() {
    List<SystemInformation> infos = Labzens.allSystemInformation();
    
    assertNotNull(infos);
  }

  @Test
  void testAllEnvironments() {
    Environments environments = Labzens.allEnvironments();
    
    assertNotNull(environments);
    assertNotNull(environments.javaVersion());
  }
}
