package cn.labzen.meta.component

import cn.labzen.meta.Labzens
import cn.labzen.meta.component.bean.Component
import cn.labzen.meta.component.bean.Meta
import java.util.*

internal class ComponentRecorder private constructor() {

  companion object {

    fun record() {
      val serviceLoader = ServiceLoader.load(LabzenComponent::class.java)

      serviceLoader.map {
        val information = Manifest(it).determine()
        val meta = Meta(it.javaClass, it)
        Component(information, meta)
      }.forEach(Labzens::addComponent)
    }
  }
}
