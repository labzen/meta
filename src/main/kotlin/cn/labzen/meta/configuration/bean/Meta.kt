package cn.labzen.meta.configuration.bean

import org.slf4j.event.Level
import java.lang.reflect.Method

data class Meta(
  val method: Method,
  val returnType: Class<*>,
  val path: String,
  val required: Boolean,
  val logLevel: Level,
  val defaultValue: String? = null,
  val description: String? = null
)
