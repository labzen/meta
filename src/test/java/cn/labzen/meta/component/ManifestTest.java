package cn.labzen.meta.component;

import cn.labzen.meta.component.bean.Information;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ManifestTest {

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
      return "Test component description";
    }
  }

  private DeclaredComponent component;
  private Manifest manifest;

  @BeforeEach
  void setup() {
    component = new TestComponent();
    manifest = new Manifest(component);
  }

  @Test
  void testManifestCreation() {
    assertNotNull(manifest);
  }

  @Test
  void testDetermineInformation() {
    Information info = manifest.determine();
    
    assertNotNull(info);
    // 应该至少有一个信息源能提供数据
    assertNotNull(info.title());
  }

  @Test
  void testFromPackage() {
    Information info = manifest.determine();
    
    // Package 信息可能为空，但不应该抛异常
    assertNotNull(info);
  }

  @Test
  void testFromMavenWhenPomExists(@TempDir Path tempDir) throws IOException {
    // 创建临时pom.xml
    File pomFile = new File("pom.xml");
    boolean pomExists = pomFile.exists();
    
    if (pomExists) {
      // 如果pom.xml存在，测试应该能读取信息
      Information info = manifest.determine();
      assertNotNull(info);
      assertNotNull(info.title());
    }
  }

  @Test
  void testDescriptionFromComponent() {
    Information info = manifest.determine();
    
    // description应该来自component
    assertEquals("Test component description", info.description());
  }

  @Test
  void testInformationNotNull() {
    Information info = manifest.determine();
    
    assertNotNull(info);
    assertNotNull(info.description());
  }

  @Test
  void testMultipleDetermineCallsReturnConsistentResults() {
    Manifest manifest1 = new Manifest(component);
    Manifest manifest2 = new Manifest(component);
    
    Information info1 = manifest1.determine();
    Information info2 = manifest2.determine();
    
    // 应该返回一致的结果
    assertEquals(info1.description(), info2.description());
  }
}
