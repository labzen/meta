package cn.labzen.meta

import cn.labzen.meta.component.bean.Component
import cn.labzen.meta.component.bean.Information
import cn.labzen.meta.system.SystemInformationCollector
import org.slf4j.LoggerFactory

internal object LabzenMetaPrinter {

  private val logger = LoggerFactory.getLogger(LabzenMetaPrinter::class.java)

  fun print() {
    printLogoAndComponents()
    printSystemInformation()
  }

  private fun printLogoAndComponents() {
    val e = "\u001B[0m"
    val k = "\u001B[38;5;214m"
    val h = "\u001B[38;5;179m"
    val t = "\u001B[38;5;184m"

    println("")
    println("$k█▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀$e")
    println("$k█$e")
    println("$k█$e  $h ██▓      ▄▄▄        ▄▄▄▄    ▒███████▒ ▓██████ ███▄     █$e")
    println("$k█$e  $h ██▒     ▒████▄     ▓█████▄  ░ ▒   ▄▀░ ▓█    ▀ ██ ▀█   ██$e")
    println("$k█$e  $h▒██░     ▒██  ▀█▄   ▒██  ▄██   ░ ▄▀ ░  ▒████   ██  ▀█  █▒$e")
    println("$k█$e  $h▒██░     ░██▄▄▄▄██  ▒██░█▀     ▄▀      ▒▓█  ▄  ██▒  ▐▌██▒$e")
    println("$k█$e  $h░█████▓▒  ▓█   ▓██  ░▓▒  ▀█▓ ▒███████▒ ░▒████▒ ██░   ▓██░$e")
    println("$k█$e  $h░ ▒░▓  ░  ▒▒   ▓▒█░ ░▒▓███▀▒ ░▒▒ ▓░▒░▒ ░░ ▒░ ░  ▒░   ▒ ▒ $e")
    println("$k█$e  $h░ ░ ▒  ░   ▒   ▒▒ ░  ░▒   ░  ░░▒ ▒ ░ ▒  ░ ░  ░  ░░   ░ ▒░$e")
    println("$k█$e  $h  ░ ░      ░   ▒     ░    ░  ░ ░ ░ ░ ░    ░      ░     ░ $e")
    println("$k█$e  $h    ░  ░       ░  ░  ░         ░ ░        ░  ░           $e")
    println("$k█$e")
    println("$k█$e $t Using Labzen Components$e")
    println("$k█$e")

    val infos = collect()
    if (infos.isEmpty()) {
      println("$k█▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄$e")
      println()
      return
    }
    val maxLength = infos.maxOf { it.title.length }

    infos.forEach {
      val title = it.title.padEnd(maxLength, ' ')
      val versionWithColor = if (it.version.endsWith("-SNAPSHOT")) {
        "\u001B[38;5;167mv${it.version}\u001B[0m"
      } else {
        "\u001B[38;5;157mv${it.version}\u001B[0m"
      }
      println("$k█$e  :: \u001B[38;5;184m$title\u001B[0m :: $versionWithColor - ${it.description}")
    }
    println("$k█▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄$e")
    println()
  }

  private fun collect(): List<Information> =
    Labzens.components().values.map(Component::information)

  private fun printSystemInformation() {
    val allSystemInformation = Labzens.allSystemInformation().filter { it.description != null }
    logger.info("当前主机信息：")

    val e = "\u001B[0m"
    val b = "\u001B[38;5;178m"
    val c = arrayOf("\u001B[38;5;107m", "\u001B[38;5;137m")

    // 加这个 1 代表的是序号后边那个点
    val indexLength = allSystemInformation.size.toString().length + 1
    val catalogMaxLength = allSystemInformation.maxOf { it.catalog.length }
    val nameMaxLength = allSystemInformation.maxOf { it.name.length }

    var lastCatalog = allSystemInformation.first().catalog
    var ci = 0
    allSystemInformation.forEachIndexed { index, si ->
      val indexString = "$index.".padStart(indexLength)
      val catalogString = si.catalog.padStart(catalogMaxLength)
      val nameString = si.name.padEnd(nameMaxLength)

      if (lastCatalog != si.catalog) {
        lastCatalog = si.catalog
        ci = ci xor 1
      }

      println("$b♎ $indexString ${c[ci]}[ $catalogString :: $nameString ]$e ${si.title}  >>>  ${si.description}")
    }

    println()
  }

  @JvmStatic
  fun main(args: Array<String>) {
    SystemInformationCollector.collect()
    LabzenMetaPrinter.print()
  }
}
