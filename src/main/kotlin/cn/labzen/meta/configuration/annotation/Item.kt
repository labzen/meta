package cn.labzen.meta.configuration.annotation

import org.slf4j.event.Level

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Item(
  val path: String = "",
  val required: Boolean = true,
  val logLevel: Level = Level.DEBUG,
  val defaultValue: String = "",
  // todo 不太需要这个属性
  val description: String = ""
)
