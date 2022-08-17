package cn.labzen.meta.component

import cn.labzen.meta.component.bean.Information
import java.io.File
import java.net.JarURLConnection
import java.security.CodeSource
import java.util.jar.Attributes
import java.util.jar.JarFile

internal class Manifest(private val componentClass: LabzenComponent) {

  fun determine(): Information {
    val cls = componentClass::class.java
    val codeSource: CodeSource? = cls.protectionDomain.codeSource

    return (codeSource?.let {
      fromCodeSource(it)
    } ?: fromPackage(cls.`package`)).apply {
      description = componentClass.description()
    }
  }

  private fun fromCodeSource(codeSource: CodeSource): Information? =
    try {
      val connection = codeSource.location.openConnection()
      val jarFile = if (connection is JarURLConnection) {
        connection.jarFile
      } else {
        JarFile(File(codeSource.location.toURI()))
      }

      fromJarFile(jarFile)
    } catch (ex: Exception) {
      null
    }

  private fun fromJarFile(jarFile: JarFile): Information =
    jarFile.use { file ->
      with(file.manifest.mainAttributes) {
        Information(
          getValue(Attributes.Name.IMPLEMENTATION_TITLE),
          getValue(Attributes.Name.IMPLEMENTATION_VENDOR),
          getValue(Attributes.Name.IMPLEMENTATION_VERSION)
        )
      }
    }

  private fun fromPackage(pck: Package): Information =
    Information(
      pck.implementationTitle ?: "",
      pck.implementationVendor ?: "",
      pck.implementationVersion ?: ""
    )
}
