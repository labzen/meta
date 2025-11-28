package cn.labzen.meta.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OperatingSystem;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SystemInformationCollector {

  private static final Logger LOGGER = LoggerFactory.getLogger(SystemInformationCollector.class);
  private static final SystemInformationCollector INSTANCE = new SystemInformationCollector();

  private static boolean collected = false;

  private final SystemInfo systemInfo = new SystemInfo();
  private final List<SystemInformation> infos = new ArrayList<>();
  private final DecimalFormat decimalFormat = new DecimalFormat("0.#");

  private SystemInformationCollector() {
    decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
  }

  public static void collect() {
    if (collected) {
      LOGGER.warn("系统信息已收集，请勿重复收集");
      return;
    }

    try {
      INSTANCE.collectOperatingSystem();
    } catch (Exception e) {
      LOGGER.error("收集操作系统信息失败", e);
    }

    try {
      INSTANCE.collectComputerSystem();
    } catch (Exception e) {
      LOGGER.error("收集计算机信息失败", e);
    }

    try {
      INSTANCE.collectMotherBoard();
    } catch (Exception e) {
      LOGGER.error("收集主板信息失败", e);
    }

    try {
      INSTANCE.collectFirmware();
    } catch (Exception e) {
      LOGGER.error("收集固件信息失败", e);
    }

    try {
      INSTANCE.collectProcessor();
    } catch (Exception e) {
      LOGGER.error("收集处理器信息失败", e);
    }

    try {
      INSTANCE.collectMemory();
    } catch (Exception e) {
      LOGGER.error("收集内存信息失败", e);
    }

    try {
      INSTANCE.collectDisks();
    } catch (Exception e) {
      LOGGER.error("收集磁盘信息失败", e);
    }

    try {
      INSTANCE.collectNetworks();
    } catch (Exception e) {
      LOGGER.error("收集网络信息失败", e);
    }

    collected = true;
  }

  public static List<SystemInformation> getAllInformation() {
    return Collections.unmodifiableList(INSTANCE.infos);
  }

  private void collectOperatingSystem() {
    String catalog = "os";
    OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
    String description = MessageFormatter.basicArrayFormat("{} {} {} {}版 ({}位)",
        new String[]{operatingSystem.getManufacturer(),
                     operatingSystem.getFamily(),
                     operatingSystem.getVersionInfo().getVersion(),
                     operatingSystem.getVersionInfo().getCodeName(),
                     String.valueOf(operatingSystem.getBitness())});
    addInformation(catalog, "pid", "系统-进程号　　", String.valueOf(operatingSystem.getProcessId()));
    addInformation(catalog, "manufacturer", "系统-操作系统　", description);
  }

  /**
   * 计算机系统代表计算机系统/产品的物理硬件，包括BIOS/固件、主板、逻辑板等。
   */
  private void collectComputerSystem() {
    ComputerSystem computerSystem = systemInfo.getHardware().getComputerSystem();
    String catalog = "hardware.computer";
    String description = computerSystem.getManufacturer() + computerSystem.getModel();
    addInformation(catalog, "manufacturer", "计算机-名称　　", description);
    addInformation(catalog, "serialNumber", "计算机-序列号　", computerSystem.getSerialNumber());
    addInformation(catalog, "hardwareUUID", "计算机-硬件标识", computerSystem.getHardwareUUID());
  }

  /**
   * 获取计算机系统底板/主板。
   */
  private void collectMotherBoard() {
    Baseboard baseboard = systemInfo.getHardware().getComputerSystem().getBaseboard();
    String catalog = "hardware.board";
    addInformation(catalog, "manufacturer", "主板-生产商　　", baseboard.getManufacturer());
    addInformation(catalog, "model", "主板-模型　　　", baseboard.getModel());
    addInformation(catalog, "version", "主板-版本　　　", baseboard.getVersion());
    addInformation(catalog, "serialNumber", "主板-序列号　　", baseboard.getSerialNumber());
  }

  /**
   * 获取计算机系统固件/BIOS。
   */
  private void collectFirmware() {
    Firmware firmware = systemInfo.getHardware().getComputerSystem().getFirmware();
    String catalog = "hardware.firmware";
    addInformation(catalog, "manufacturer", "BIOS-生产商 　", firmware.getManufacturer());
    addInformation(catalog, "name", "BIOS-名称 　　", firmware.getName());
    addInformation(catalog, "version", "BIOS-版本 　　", firmware.getVersion());
    addInformation(catalog, "releaseDate", "BIOS-发布日期 ", firmware.getReleaseDate());
  }

  /**
   * CPU的标识字符串，包括名称、供应商、步长、模型和族信息(也称为CPU的签名)。
   */
  private void collectProcessor() {
    CentralProcessor processor = systemInfo.getHardware().getProcessor();
    String count = MessageFormatter.basicArrayFormat("物理CPU：{}（个），逻辑CPU：{}（个）",
        new Object[]{processor.getPhysicalProcessorCount(), processor.getLogicalProcessorCount()});
    addInformation("hardware.processor", "count", "CPU-数量", count);

    CentralProcessor.ProcessorIdentifier identifier = processor.getProcessorIdentifier();
    String catalog = "hardware.processor.identifier";
    String architecture = identifier.getMicroarchitecture() + (identifier.isCpu64bit() ? "x64" : "x32");
    addInformation(catalog, "processorID", "CPU-签名", identifier.getProcessorID());
    addInformation(catalog, "vendor", "CPU-厂商", identifier.getVendor());
    addInformation(catalog, "name", "CPU-名称", identifier.getName());
    addInformation(catalog, "identifier", "CPU-标识", identifier.getIdentifier());
    addInformation(catalog, "architecture", "CPU-架构", architecture);
    addInformation(catalog, "vendorFreq", "CPU-频率", calculateHZ(identifier.getVendorFreq()));
  }

  /**
   * 计算机物理内存(RAM)以及任何可用虚拟内存的使用信息。
   */
  private void collectMemory() {
    GlobalMemory memory = systemInfo.getHardware().getMemory();
    String catalog = "hardware.memory";
    addInformation(catalog, "total", "内存-大小　", calculateGB(memory.getTotal()));
    addInformation(catalog, "pageSize", "内存-内存页", calculateGB(memory.getPageSize()));

    String catalogPhysical = "hardware.memory.physicals";

    List<PhysicalMemory> physicalMemories = memory.getPhysicalMemory();
    int maxIndexLength = String.valueOf(physicalMemories.size()).length();
    String indexFormatPattern = "物理内存-%-" + maxIndexLength + "s ";
    for (int i = 0; i < physicalMemories.size(); i++) {
      PhysicalMemory physical = physicalMemories.get(i);
      String indexString = String.format(indexFormatPattern, i);

      addInformation(catalogPhysical, i + ".manufacturer", indexString + " 生产商　", physical.getManufacturer());
      addInformation(catalogPhysical,
          i + ".memoryType",
          indexString + " 类型　　",
          physical.getMemoryType() + " " + calculateGB(physical.getCapacity()));
      addInformation(catalogPhysical, i + ".bankLabel", indexString + " 插槽　　", physical.getBankLabel());
      addInformation(catalogPhysical,
          i + ".clockSpeed",
          indexString + " 时钟频率",
          calculateMHZ(physical.getClockSpeed()));
    }
  }

  /**
   * 表示物理硬盘或其他类似的存储设备。
   */
  private void collectDisks() {
    List<HWDiskStore> stores = systemInfo.getHardware().getDiskStores();
    String catalog = "hardware.disks";
    int index = 0;
    for (HWDiskStore store : stores) {
      addInformation(catalog, index + ".name", "磁盘-" + index + " 名称　", store.getName());
      addInformation(catalog, index + ".model", "磁盘-" + index + " 模型　", store.getModel());
      addInformation(catalog, index + ".serial", "磁盘-" + index + " 序列号", store.getSerial());
      addInformation(catalog, index + ".size", "磁盘-" + index + " 大小　", calculateGB(store.getSize()));
      index++;
    }
  }

  /**
   * 网络接口。该列表不包括本地接口
   */
  private void collectNetworks() {
    String catalog = "hardware.networks";
    List<NetworkIF> networkIFs = systemInfo.getHardware().getNetworkIFs();
    int maxIndexLength = String.valueOf(networkIFs.size()).length();
    String indexFormatPattern = "网卡-%-" + maxIndexLength + "s ";
    for (int i = 0; i < networkIFs.size(); i++) {
      NetworkIF network = networkIFs.get(i);
      String indexString = String.format(indexFormatPattern, i);

      addInformation(catalog,
          i + ".name",
          indexString + " 名称　　　",
          network.getName() + " (alias: " + network.getIfAlias() + ")");
      addInformation(catalog, i + ".display", indexString + " 接口描述　", network.getDisplayName());
      addInformation(catalog, i + ".address.ipv4", indexString + " IPv4 　　", String.join(", ", network.getIPv4addr()));
      addInformation(catalog, i + ".address.ipv6", indexString + " IPv6 　　", String.join(", ", network.getIPv6addr()));
      addInformation(catalog, i + ".mac", indexString + " 物理地址　", network.getMacaddr());
    }
  }

  private void addInformation(String catalog, String name, String title, String description) {
    infos.add(new SystemInformation(catalog, name, title, description));
  }

  private String calculateGB(Long bytes) {
    double result = ((double) bytes) / 1024 / 1024 / 1024;
    return decimalFormat.format(result) + " GB";
  }

  private String calculateHZ(Long bytes) {
    double result = ((double) bytes) / 1000 / 1000 / 1000;
    return decimalFormat.format(result) + " GHz";
  }

  private String calculateMHZ(Long bytes) {
    double result = ((double) bytes) / 1000 / 1000;
    return decimalFormat.format(result) + " MHz";
  }
}
