package cn.labzen.meta

import cn.labzen.meta.component.bean.Component
import java.util.*

object Labzens {

  private val components = mutableMapOf<String, Component>()

  internal fun addComponent(component: Component) {
    components[component.information.title] = component
  }

  fun component(title: String) =
    Optional.ofNullable(components[title])

  fun components() =
    components.toMap()
}
