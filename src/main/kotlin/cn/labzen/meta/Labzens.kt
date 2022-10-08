package cn.labzen.meta

import cn.labzen.meta.component.bean.Component
import cn.labzen.meta.configuration.ConfigurationProcessor
import cn.labzen.meta.environment.bean.Environments
import cn.labzen.meta.system.bean.SystemInformation
import java.util.*

object Labzens {

  private val components = mutableMapOf<String, Component>()
  private val systemInformationList = mutableListOf<SystemInformation>()
  private lateinit var environments: Environments

  internal fun addComponent(component: Component) {
    components[component.information.title] = component
  }

  @JvmStatic
  fun component(title: String) =
    Optional.ofNullable(components[title])

  @JvmStatic
  fun components() =
    components.toMap()

  fun <CI> configurationWith(inter: Class<CI>): CI =
    ConfigurationProcessor.getInterfaceProxy(inter)

  internal fun addSystemInformation(systemInformation: SystemInformation) {
    systemInformationList.add(systemInformation)
  }

  @JvmStatic
  fun allSystemInformation() =
    systemInformationList.toList()

  internal fun setEnvironment(environments: Environments) {
    this.environments = environments
  }

  @JvmStatic
  fun environment() = environments
}
