package cn.labzen.meta.component;

public interface DeclaredComponent {

  /**
   * Labzen组件的唯一标识
   */
  String mark();

  /**
   * Labzen组件的包根路径
   */
  String packageBased();

  /**
   * Labzen组件简述
   */
  String description();
}
