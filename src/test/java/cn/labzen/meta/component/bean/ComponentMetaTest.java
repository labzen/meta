package cn.labzen.meta.component.bean;

import cn.labzen.meta.component.DeclaredComponent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComponentMetaTest {

  private static class TestComponent implements DeclaredComponent {
    @Override
    public String mark() {
      return "test-component";
    }

    @Override
    public String packageBased() {
      return "cn.labzen.test";
    }

    @Override
    public String description() {
      return "Test component";
    }
  }

  @Test
  void testComponentMetaCreation() {
    Information info = new Information("Test Component", "Labzen", "1.0.0", "Test description");
    DeclaredComponent component = new TestComponent();
    
    ComponentMeta meta = new ComponentMeta(info, component);
    
    assertNotNull(meta);
    assertEquals(info, meta.information());
    assertEquals(component, meta.component());
  }

  @Test
  void testComponentMetaWithNull() {
    Information info = new Information("Test", "Vendor", "1.0", "Desc");
    
    // Record可以接受null值，尽管标记了@Nonnull
    ComponentMeta meta1 = new ComponentMeta(null, new TestComponent());
    assertNull(meta1.information());
    
    ComponentMeta meta2 = new ComponentMeta(info, null);
    assertNull(meta2.component());
  }

  @Test
  void testRecordEquality() {
    Information info1 = new Information("Test", "Vendor", "1.0", "Desc");
    Information info2 = new Information("Test", "Vendor", "1.0", "Desc");
    DeclaredComponent component = new TestComponent();
    
    ComponentMeta meta1 = new ComponentMeta(info1, component);
    ComponentMeta meta2 = new ComponentMeta(info2, component);
    
    assertEquals(meta1, meta2);
    assertEquals(meta1.hashCode(), meta2.hashCode());
  }

  @Test
  void testRecordToString() {
    Information info = new Information("Test", "Vendor", "1.0", "Desc");
    DeclaredComponent component = new TestComponent();
    ComponentMeta meta = new ComponentMeta(info, component);
    
    String toString = meta.toString();
    
    assertNotNull(toString);
    assertTrue(toString.contains("ComponentMeta"));
    assertTrue(toString.contains("Test"));
  }
}
