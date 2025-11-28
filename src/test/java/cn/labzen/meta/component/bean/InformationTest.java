package cn.labzen.meta.component.bean;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InformationTest {

  @Test
  void testInformationCreation() {
    Information info = new Information("Labzen Meta", "Labzen", "1.0.0", "Meta information module");
    
    assertEquals("Labzen Meta", info.title());
    assertEquals("Labzen", info.vendor());
    assertEquals("1.0.0", info.version());
    assertEquals("Meta information module", info.description());
  }

  @Test
  void testInformationWithNullTitle() {
    // Record可以接受null值，尽管标记了@Nonnull
    Information info = new Information(null, "Vendor", "1.0", "Desc");
    assertNull(info.title());
  }

  @Test
  void testInformationWithNullVendor() {
    Information info = new Information("Title", null, "1.0", "Desc");
    
    assertEquals("Title", info.title());
    assertNull(info.vendor());
  }

  @Test
  void testInformationWithNullVersion() {
    Information info = new Information("Title", "Vendor", null, "Desc");
    
    assertEquals("Title", info.title());
    assertNull(info.version());
  }

  @Test
  void testInformationWithNullDescription() {
    Information info = new Information("Title", "Vendor", "1.0", null);
    
    assertEquals("Title", info.title());
    assertNull(info.description());
  }

  @Test
  void testRecordEquality() {
    Information info1 = new Information("Test", "Vendor", "1.0", "Desc");
    Information info2 = new Information("Test", "Vendor", "1.0", "Desc");
    
    assertEquals(info1, info2);
    assertEquals(info1.hashCode(), info2.hashCode());
  }

  @Test
  void testRecordInequality() {
    Information info1 = new Information("Test1", "Vendor", "1.0", "Desc");
    Information info2 = new Information("Test2", "Vendor", "1.0", "Desc");
    
    assertNotEquals(info1, info2);
  }

  @Test
  void testRecordToString() {
    Information info = new Information("Test", "Vendor", "1.0", "Description");
    String toString = info.toString();
    
    assertNotNull(toString);
    assertTrue(toString.contains("Information"));
    assertTrue(toString.contains("Test"));
    assertTrue(toString.contains("Vendor"));
    assertTrue(toString.contains("1.0"));
  }

  @Test
  void testEmptyStrings() {
    Information info = new Information("", "", "", "");
    
    assertEquals("", info.title());
    assertEquals("", info.vendor());
    assertEquals("", info.version());
    assertEquals("", info.description());
  }
}
