package cn.labzen.meta.meta

import cn.labzen.meta.component.LabzenComponent

class MetaMeta : LabzenComponent {

  override fun mark(): String =
    "Labzen-Meta"

  override fun packageBased(): String =
    "cn.labzen.meta"

  override fun description(): String =
    "收集、管理、报告Labzen组件的元信息"
}
