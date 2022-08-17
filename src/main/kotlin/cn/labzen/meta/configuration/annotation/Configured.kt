package cn.labzen.meta.configuration.annotation

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Configured(
  val namespace: String
)
