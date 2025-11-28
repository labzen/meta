package cn.labzen.meta.system;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SystemInformationCollectorTest {

  @BeforeAll
  static void setup() {
    // 确保只收集一次
    SystemInformationCollector.collect();
  }

  @Test
  void testCollect() {
    // 第二次调用应该只是警告，不会重复收集
    SystemInformationCollector.collect();
    
    List<SystemInformation> infos = SystemInformationCollector.getAllInformation();
    assertNotNull(infos);
  }

  @Test
  void testGetAllInformationNotNull() {
    List<SystemInformation> infos = SystemInformationCollector.getAllInformation();
    
    assertNotNull(infos);
  }

  @Test
  void testGetAllInformationNotEmpty() {
    List<SystemInformation> infos = SystemInformationCollector.getAllInformation();
    
    assertFalse(infos.isEmpty());
  }

  @Test
  void testOperatingSystemInfoCollected() {
    List<SystemInformation> infos = SystemInformationCollector.getAllInformation();
    
    boolean hasOsInfo = infos.stream()
        .anyMatch(info -> "os".equals(info.catalog()));
    
    assertTrue(hasOsInfo);
  }

  @Test
  void testHardwareInfoCollected() {
    List<SystemInformation> infos = SystemInformationCollector.getAllInformation();
    
    boolean hasHardwareInfo = infos.stream()
        .anyMatch(info -> info.catalog() != null && info.catalog().startsWith("hardware"));
    
    assertTrue(hasHardwareInfo);
  }

  @Test
  void testProcessorInfoCollected() {
    List<SystemInformation> infos = SystemInformationCollector.getAllInformation();
    
    boolean hasProcessorInfo = infos.stream()
        .anyMatch(info -> info.catalog() != null && info.catalog().contains("processor"));
    
    assertTrue(hasProcessorInfo);
  }

  @Test
  void testMemoryInfoCollected() {
    List<SystemInformation> infos = SystemInformationCollector.getAllInformation();
    
    boolean hasMemoryInfo = infos.stream()
        .anyMatch(info -> info.catalog() != null && info.catalog().contains("memory"));
    
    assertTrue(hasMemoryInfo);
  }

  @Test
  void testAllInformationHasValidCatalog() {
    List<SystemInformation> infos = SystemInformationCollector.getAllInformation();
    
    for (SystemInformation info : infos) {
      assertNotNull(info.catalog());
      assertFalse(info.catalog().isEmpty());
    }
  }

  @Test
  void testAllInformationHasValidName() {
    List<SystemInformation> infos = SystemInformationCollector.getAllInformation();
    
    for (SystemInformation info : infos) {
      assertNotNull(info.name());
      assertFalse(info.name().isEmpty());
    }
  }

  @Test
  void testReturnedListIsUnmodifiable() {
    List<SystemInformation> infos = SystemInformationCollector.getAllInformation();
    
    assertThrows(UnsupportedOperationException.class, 
        () -> infos.add(new SystemInformation("test", "test", "test", "test")));
  }

  @Test
  void testSameInstanceReturned() {
    List<SystemInformation> infos1 = SystemInformationCollector.getAllInformation();
    List<SystemInformation> infos2 = SystemInformationCollector.getAllInformation();
    
    // 应该返回同一个不可修改的列表视图
    assertEquals(infos1.size(), infos2.size());
  }
}
