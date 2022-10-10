package cn.labzen.meta.configuration

interface LabzenConfigurationFileResolver {

  @Throws(RuntimeException::class)
  fun resolve(): Map<String, Any>
}
