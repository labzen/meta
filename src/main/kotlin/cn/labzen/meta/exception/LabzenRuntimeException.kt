package cn.labzen.meta.exception

import org.slf4j.helpers.MessageFormatter

abstract class LabzenRuntimeException : RuntimeException {
  constructor(message: String) : super(message)
  constructor(message: String, vararg arguments: Any?) : super(MessageFormatter.basicArrayFormat(message, arguments))

  constructor(cause: Throwable) : super(cause)
  constructor(cause: Throwable, message: String) : super(message, cause)
  constructor(cause: Throwable, message: String, vararg arguments: Any?) :
      super(MessageFormatter.basicArrayFormat(message, arguments), cause)
}
