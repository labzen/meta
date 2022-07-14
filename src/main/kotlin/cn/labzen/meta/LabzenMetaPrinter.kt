package cn.labzen.meta

import cn.labzen.meta.component.bean.Component
import cn.labzen.meta.component.bean.Information

internal object LabzenMetaPrinter {

  fun print() {
    printLogoAndComponents()
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
    Labzens.components.values.map(Component::information)
}
