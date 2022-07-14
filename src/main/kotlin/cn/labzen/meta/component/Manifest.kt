package cn.labzen.meta.component

import cn.labzen.meta.LabzenMeta
import cn.labzen.meta.component.bean.Information
import java.io.File
import java.net.JarURLConnection
import java.security.CodeSource
import java.util.jar.Attributes
import java.util.jar.JarFile

internal object Manifest {

  private val UNIDENTIFIED_INFORMATION = Information("", "", "")

  fun determine(meta: LabzenMeta): Information {
    val cls = meta::class.java
    val codeSource: CodeSource? = cls.protectionDomain.codeSource

    return (codeSource?.let {
      fromCodeSource(it)
    } ?: fromPackage(cls.`package`)).apply {
      description = meta.description()
    }
  }

  private fun fromCodeSource(codeSource: CodeSource): Information =
    try {
      val connection = codeSource.location.openConnection()
      val jarFile = if (connection is JarURLConnection) {
        connection.jarFile
      } else {
        JarFile(File(codeSource.location.toURI()))
      }

      fromJarFile(jarFile)
    } catch (ex: Exception) {
      UNIDENTIFIED_INFORMATION
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
    Information(pck.implementationTitle, pck.implementationVendor, pck.implementationVersion)

}
