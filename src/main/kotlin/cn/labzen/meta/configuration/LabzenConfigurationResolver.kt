package cn.labzen.meta.configuration

interface LabzenConfigurationResolver {

  @Throws(RuntimeException::class)
  fun resolve(): Map<String, Any>
}
