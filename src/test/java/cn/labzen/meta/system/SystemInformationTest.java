package cn.labzen.meta.system;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemInformationTest {

  @Test
  void testSystemInformationCreation() {
    SystemInformation info = new SystemInformation(
        "hardware.processor",
        "cpu-name",
        "CPU-名称",
        "Intel Core i7"
    );
    
    assertEquals("hardware.processor", info.catalog());
    assertEquals("cpu-name", info.name());
    assertEquals("CPU-名称", info.title());
    assertEquals("Intel Core i7", info.description());
  }

  @Test
  void testSystemInformationWithNullValues() {
    SystemInformation info = new SystemInformation(null, null, null, null);
    
    assertNull(info.catalog());
    assertNull(info.name());
    assertNull(info.title());
    assertNull(info.description());
  }

  @Test
  void testRecordEquality() {
    SystemInformation info1 = new SystemInformation("cat", "name", "title", "desc");
    SystemInformation info2 = new SystemInformation("cat", "name", "title", "desc");
    
    assertEquals(info1, info2);
    assertEquals(info1.hashCode(), info2.hashCode());
  }

  @Test
  void testRecordInequality() {
    SystemInformation info1 = new SystemInformation("cat1", "name", "title", "desc");
    SystemInformation info2 = new SystemInformation("cat2", "name", "title", "desc");
    
    assertNotEquals(info1, info2);
  }

  @Test
  void testRecordToString() {
    SystemInformation info = new SystemInformation("catalog", "name", "title", "description");
    
    String toString = info.toString();
    assertNotNull(toString);
    assertTrue(toString.contains("SystemInformation"));
    assertTrue(toString.contains("catalog"));
  }

  @Test
  void testEmptyStrings() {
    SystemInformation info = new SystemInformation("", "", "", "");
    
    assertEquals("", info.catalog());
    assertEquals("", info.name());
    assertEquals("", info.title());
    assertEquals("", info.description());
  }
}
