package cn.labzen.meta.environment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnvironmentCollectorTest {

  @Test
  void testGetEnvironments() {
    Environments environments = EnvironmentCollector.getEnvironments();
    
    assertNotNull(environments);
  }

  @Test
  void testJavaVersionNotNull() {
    Environments environments = EnvironmentCollector.getEnvironments();
    
    assertNotNull(environments.javaVersion());
    assertFalse(environments.javaVersion().isEmpty());
  }

  @Test
  void testJavaVendorNotNull() {
    Environments environments = EnvironmentCollector.getEnvironments();
    
    assertNotNull(environments.javaVendor());
  }

  @Test
  void testJavaHomeNotNull() {
    Environments environments = EnvironmentCollector.getEnvironments();
    
    assertNotNull(environments.javaHome());
    assertFalse(environments.javaHome().isEmpty());
  }

  @Test
  void testUserHomeNotNull() {
    Environments environments = EnvironmentCollector.getEnvironments();
    
    assertNotNull(environments.userHome());
    assertFalse(environments.userHome().isEmpty());
  }

  @Test
  void testUserDirNotNull() {
    Environments environments = EnvironmentCollector.getEnvironments();
    
    assertNotNull(environments.userDir());
    assertFalse(environments.userDir().isEmpty());
  }

  @Test
  void testPathSeparatorNotNull() {
    Environments environments = EnvironmentCollector.getEnvironments();
    
    assertNotNull(environments.pathSeparator());
    assertFalse(environments.pathSeparator().isEmpty());
  }

  @Test
  void testFileSeparatorNotNull() {
    Environments environments = EnvironmentCollector.getEnvironments();
    
    assertNotNull(environments.fileSeparator());
    assertFalse(environments.fileSeparator().isEmpty());
  }

  @Test
  void testLineSeparatorNotNull() {
    Environments environments = EnvironmentCollector.getEnvironments();
    
    assertNotNull(environments.lineSeparator());
    assertFalse(environments.lineSeparator().isEmpty());
  }

  @Test
  void testSingleton() {
    Environments env1 = EnvironmentCollector.getEnvironments();
    Environments env2 = EnvironmentCollector.getEnvironments();
    
    assertSame(env1, env2);
  }

  @Test
  void testIoTempPathNotNull() {
    Environments environments = EnvironmentCollector.getEnvironments();
    
    assertNotNull(environments.ioTempPath());
  }
}
