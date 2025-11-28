package cn.labzen.meta.meta;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetaMetaTest {

  @Test
  void testMark() {
    MetaMeta metaMeta = new MetaMeta();
    
    assertEquals("Labzen-Meta", metaMeta.mark());
  }

  @Test
  void testPackageBased() {
    MetaMeta metaMeta = new MetaMeta();
    
    assertEquals("cn.labzen.meta", metaMeta.packageBased());
  }

  @Test
  void testDescription() {
    MetaMeta metaMeta = new MetaMeta();
    
    assertEquals("收集、管理、报告Labzen组件的元信息", metaMeta.description());
  }

  @Test
  void testImplementsDeclaredComponent() {
    MetaMeta metaMeta = new MetaMeta();
    
    assertNotNull(metaMeta.mark());
    assertNotNull(metaMeta.packageBased());
    assertNotNull(metaMeta.description());
  }

  @Test
  void testConsistentValues() {
    MetaMeta metaMeta1 = new MetaMeta();
    MetaMeta metaMeta2 = new MetaMeta();
    
    assertEquals(metaMeta1.mark(), metaMeta2.mark());
    assertEquals(metaMeta1.packageBased(), metaMeta2.packageBased());
    assertEquals(metaMeta1.description(), metaMeta2.description());
  }
}
