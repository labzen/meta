package cn.labzen.meta.meta;

import cn.labzen.meta.component.DeclaredComponent;

public class MetaMeta implements DeclaredComponent {

  @Override
  public String mark() {
    return "Labzen-Meta";
  }

  @Override
  public String packageBased() {
    return "cn.labzen.meta";
  }

  @Override
  public String description() {
    return "收集、管理、报告Labzen组件的元信息";
  }
}
