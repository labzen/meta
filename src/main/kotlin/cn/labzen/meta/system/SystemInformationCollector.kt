package cn.labzen.meta.system

import cn.labzen.meta.Labzens.addSystemInformation
import cn.labzen.meta.system.bean.SystemInformation
import oshi.SystemInfo
import java.math.RoundingMode
import java.text.DecimalFormat

internal class SystemInformationCollector private constructor(si: SystemInfo) {

  private val hardware = si.hardware
  private val operatingSystem = si.operatingSystem

  fun collect() {
    collectOperatingSystem()

    collectComputerSystem()
    collectMotherBoard()
    collectFirmware()
    collectProcessor()
    collectMemory()
    collectDisks()
    collectNetworks()
  }

  private fun collectOperatingSystem() {
    val catalog = "os"
    with(operatingSystem) {
      val description = "$manufacturer $family ${versionInfo.version} ${versionInfo.codeName}版 (${bitness}位)"
      addSystemInformation(SystemInformation(catalog, "pid", "系统-进程号　", processId.toString()))
      addSystemInformation(SystemInformation(catalog, "manufacturer", "系统-操作系统", description))
    }
  }

  /**
   * 计算机系统代表计算机系统/产品的物理硬件，包括BIOS/固件、主板、逻辑板等。
   */
  private fun collectComputerSystem() {
    val cs = hardware.computerSystem
    val catalog = "hardware.computer"
    with(cs) {
      val description = "$manufacturer $model"
      addSystemInformation(SystemInformation(catalog, "manufacturer", "计算机-名称　　　　", description))
      addSystemInformation(SystemInformation(catalog, "serialNumber", "计算机-序列号　　　", serialNumber))
      addSystemInformation(SystemInformation(catalog, "hardwareUUID", "计算机-硬件唯一标识", hardwareUUID))
    }
  }

  /**
   * 获取计算机系统底板/主板。
   */
  private fun collectMotherBoard() {
    val baseboard = hardware.computerSystem.baseboard
    val catalog = "hardware.board"
    with(baseboard) {
      addSystemInformation(SystemInformation(catalog, "manufacturer", "主板-生产商", manufacturer))
      addSystemInformation(SystemInformation(catalog, "model", "主板-模型　", model))
      addSystemInformation(SystemInformation(catalog, "version", "主板-版本　", version))
      addSystemInformation(SystemInformation(catalog, "serialNumber", "主板-序列号", serialNumber))
    }
  }

  /**
   * 获取计算机系统固件/BIOS。
   */
  private fun collectFirmware() {
    val firmware = hardware.computerSystem.firmware
    val catalog = "hardware.firmware"
    with(firmware) {
      addSystemInformation(SystemInformation(catalog, "manufacturer", "BIOS固件-生产商　", manufacturer))
      addSystemInformation(SystemInformation(catalog, "name", "BIOS固件-名称　　", name))
      addSystemInformation(SystemInformation(catalog, "version", "BIOS固件-版本　　", version))
      addSystemInformation(SystemInformation(catalog, "releaseDate", "BIOS固件-发布日期", releaseDate))
    }
  }

  /**
   * CPU的标识字符串，包括名称、供应商、步长、模型和族信息(也称为CPU的签名)。
   */
  private fun collectProcessor() {
    val processor = hardware.processor
    val catalogProcessor = "hardware.processor"
    val count = "物理CPU：${processor.physicalProcessorCount}（个），逻辑CPU：${processor.logicalProcessorCount}（个）"
    addSystemInformation(SystemInformation(catalogProcessor, "count", "CPU-数量", count))

    val processorIdentifier = processor.processorIdentifier
    val catalogIdentifier = "hardware.processor.identifier"
    with(processorIdentifier) {
      addSystemInformation(SystemInformation(catalogIdentifier, "processorID", "CPU-签名", processorID))
      addSystemInformation(SystemInformation(catalogIdentifier, "vendor", "CPU-厂商", vendor))
      addSystemInformation(SystemInformation(catalogIdentifier, "name", "CPU-名称", name))
      addSystemInformation(SystemInformation(catalogIdentifier, "identifier", "CPU-标识", identifier))
      val architecture = "$microarchitecture ${if (isCpu64bit) "x64" else "x32" }"
      addSystemInformation(SystemInformation(catalogIdentifier, "architecture", "CPU-架构", architecture))
      addSystemInformation(SystemInformation(catalogIdentifier, "vendorFreq", "CPU-频率", calculateHz(vendorFreq)))
    }
  }

  /**
   * 计算机物理内存(RAM)以及任何可用虚拟内存的使用信息。
   */
  private fun collectMemory() {
    val memory = hardware.memory
    val catalog = "hardware.memory"
    with(memory) {
      addSystemInformation(SystemInformation(catalog, "total", "内存-大小", calculateGB(total)))
      addSystemInformation(SystemInformation(catalog, "available", "内存-可用", calculateGB(available)))
    }

    val catalogPhysical = "hardware.memory.physicals"
    memory.physicalMemory.forEachIndexed { i, pm ->
      addSystemInformation(SystemInformation(catalogPhysical, "$i.manufacturer", "物理内存-($i) 生产商　", pm.manufacturer))
      val description = "${pm.memoryType} ${calculateGB(pm.capacity)}"
      addSystemInformation(SystemInformation(catalogPhysical, "$i.memoryType", "物理内存-($i) 类型　　", description))
      addSystemInformation(SystemInformation(catalogPhysical, "$i.bankLabel", "物理内存-($i) 插槽　　", pm.bankLabel))
      val clockSpeed = "${pm.clockSpeed / 1000 / 1000} MHz"
      addSystemInformation(SystemInformation(catalogPhysical, "$i.clockSpeed", "物理内存-($i) 时钟频率", clockSpeed))
    }
  }

  /**
   * 表示物理硬盘或其他类似的存储设备。
   */
  private fun collectDisks() {
    val diskStores = hardware.diskStores
    val catalog = "hardware.disks"
    diskStores.forEachIndexed { i, ds ->
      addSystemInformation(SystemInformation(catalog, "$i.name", "磁盘-($i) 名称　", ds.name))
      addSystemInformation(SystemInformation(catalog, "$i.model", "磁盘-($i) 模型　", ds.model))
      addSystemInformation(SystemInformation(catalog, "$i.serial", "磁盘-($i) 序列号", ds.serial))
      addSystemInformation(SystemInformation(catalog, "$i.size", "磁盘-($i) 大小　", calculateGB(ds.size)))
    }
  }

  /**
   * 网络接口。该列表不包括本地接口
   */
  private fun collectNetworks() {
    val networkIFs = hardware.networkIFs
    val catalog = "hardware.networks"
    networkIFs.forEachIndexed { i, nif ->
      val name = "${nif.name} (alias: ${nif.ifAlias})"
      addSystemInformation(SystemInformation(catalog, "$i.name", "网卡-($i) 名称　　", name))
      addSystemInformation(SystemInformation(catalog, "$i.display", "网卡-($i) 接口描述", nif.displayName))
      val ipv4 = nif.iPv4addr.joinToString(" ,")
      addSystemInformation(SystemInformation(catalog, "$i.address.ipv4", "网卡-($i) IPv4　 ", ipv4))
      val ipv6 = nif.iPv6addr.joinToString(" ,")
      addSystemInformation(SystemInformation(catalog, "$i.address.ipv6", "网卡-($i) IPv6　 ", ipv6))
      addSystemInformation(SystemInformation(catalog, "$i.mac", "网卡-($i) 物理地址", nif.macaddr))
    }
  }

  private fun calculateGB(bytes: Long): String {
    val result = bytes.toDouble() / 1024 / 1024 / 1024
    return decimalFormat.format(result) + " GB"
  }

  private fun calculateHz(bytes: Long): String {
    val result = bytes.toDouble() / 1000 / 1000 / 1000
    return decimalFormat.format(result) + " GHz"
  }

  companion object {
    private val decimalFormat = DecimalFormat("0.#").apply {
      roundingMode = RoundingMode.CEILING
    }

    fun collect() {
      SystemInformationCollector(SystemInfo()).collect()
    }
  }
}
