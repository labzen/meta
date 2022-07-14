package cn.labzen.meta

import cn.labzen.meta.component.bean.Component
import cn.labzen.meta.component.Manifest
import cn.labzen.meta.component.bean.Meta
import java.util.*

object Labzens {

  lateinit var components: Map<String, Component>

  fun recording() {
    val serviceLoader = ServiceLoader.load(LabzenMeta::class.java)

    components = serviceLoader.map {
      val information = Manifest.determine(it)
      val meta = Meta(it.javaClass, it)
      Component(information, meta)
    }.associateBy {
      it.information.title
    }
  }
}
