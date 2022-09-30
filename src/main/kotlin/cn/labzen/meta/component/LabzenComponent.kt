package cn.labzen.meta.component

interface LabzenComponent {

  /**
   * Labzen组件的唯一标识
   */
  fun mark(): String

  /**
   * Labzen组件的包根路径
   */
  fun packageBased(): String

  /**
   * Labzen组件简述
   */
  fun description(): String
}
