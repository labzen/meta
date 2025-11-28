package cn.labzen.meta.environment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnvironmentsTest {

  @Test
  void testEnvironmentsCreation() {
    Environments env = new Environments(
        "17.0.1", 
        "Oracle Corporation",
        "https://java.oracle.com",
        "C:\\Program Files\\Java\\jdk-17",
        "61.0",
        "classpath",
        "lib",
        ";",
        "\\",
        "\r\n",
        "C:\\Users\\test",
        "C:\\Working",
        "C:\\Temp"
    );
    
    assertEquals("17.0.1", env.javaVersion());
    assertEquals("Oracle Corporation", env.javaVendor());
    assertEquals("https://java.oracle.com", env.javaVendorUrl());
    assertEquals("C:\\Program Files\\Java\\jdk-17", env.javaHome());
    assertEquals("61.0", env.classVersion());
    assertEquals("classpath", env.classpath());
    assertEquals("lib", env.libraryPath());
    assertEquals(";", env.pathSeparator());
    assertEquals("\\", env.fileSeparator());
    assertEquals("\r\n", env.lineSeparator());
    assertEquals("C:\\Users\\test", env.userHome());
    assertEquals("C:\\Working", env.userDir());
    assertEquals("C:\\Temp", env.ioTempPath());
  }

  @Test
  void testEnvironmentsWithNullValues() {
    Environments env = new Environments(
        null, null, null, null, null, null, null, null, null, null, null, null, null
    );
    
    assertNull(env.javaVersion());
    assertNull(env.javaVendor());
    assertNull(env.javaHome());
  }

  @Test
  void testRecordEquality() {
    Environments env1 = new Environments(
        "17", "Oracle", "url", "home", "61", "cp", "lib", ";", "\\", "\n", "home", "dir", "tmp"
    );
    Environments env2 = new Environments(
        "17", "Oracle", "url", "home", "61", "cp", "lib", ";", "\\", "\n", "home", "dir", "tmp"
    );
    
    assertEquals(env1, env2);
    assertEquals(env1.hashCode(), env2.hashCode());
  }

  @Test
  void testRecordToString() {
    Environments env = new Environments(
        "17", "Oracle", "url", "home", "61", "cp", "lib", ";", "\\", "\n", "home", "dir", "tmp"
    );
    
    String toString = env.toString();
    assertNotNull(toString);
    assertTrue(toString.contains("Environments"));
    assertTrue(toString.contains("17"));
    assertTrue(toString.contains("Oracle"));
  }
}
