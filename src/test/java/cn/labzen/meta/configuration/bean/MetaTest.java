package cn.labzen.meta.configuration.bean;

import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class MetaTest {

  private static class TestClass {
    public String testMethod() {
      return "test";
    }
  }

  @Test
  void testMetaCreation() throws NoSuchMethodException {
    Method method = TestClass.class.getMethod("testMethod");
    Meta meta = new Meta(method, String.class, "test.path", true, Level.INFO, "default");
    
    assertEquals(method, meta.method());
    assertEquals(String.class, meta.returnType());
    assertEquals("test.path", meta.path());
    assertTrue(meta.required());
    assertEquals(Level.INFO, meta.logLevel());
    assertEquals("default", meta.defaultValue());
  }

  @Test
  void testMetaWithNullDefaultValue() throws NoSuchMethodException {
    Method method = TestClass.class.getMethod("testMethod");
    Meta meta = new Meta(method, String.class, "test.path", false, Level.DEBUG, null);
    
    assertFalse(meta.required());
    assertNull(meta.defaultValue());
  }

  @Test
  void testRecordEquality() throws NoSuchMethodException {
    Method method = TestClass.class.getMethod("testMethod");
    Meta meta1 = new Meta(method, String.class, "path", true, Level.INFO, "value");
    Meta meta2 = new Meta(method, String.class, "path", true, Level.INFO, "value");
    
    assertEquals(meta1, meta2);
    assertEquals(meta1.hashCode(), meta2.hashCode());
  }

  @Test
  void testRecordToString() throws NoSuchMethodException {
    Method method = TestClass.class.getMethod("testMethod");
    Meta meta = new Meta(method, String.class, "test.path", true, Level.INFO, "default");
    
    String toString = meta.toString();
    assertNotNull(toString);
    assertTrue(toString.contains("Meta"));
  }
}
