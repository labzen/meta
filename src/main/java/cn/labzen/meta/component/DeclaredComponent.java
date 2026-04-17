package cn.labzen.meta.component;

/**
 * 声明式组件接口
 * <p>
 * 所有Labzen模块需要实现的标记接口，用于组件注册和元数据收集。
 * 通过ServiceLoader机制发现并加载实现了此接口的组件。
 */
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
