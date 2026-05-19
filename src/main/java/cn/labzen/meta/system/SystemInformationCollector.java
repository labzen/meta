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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 系统硬件信息收集器
 * <p>
 * 使用OSHI库收集计算机的硬件信息，包括：
 * 操作系统、计算机系统、主板、固件、处理器、内存、磁盘、网络等。
 * 采用单例模式，只在首次调用collect()时执行信息收集。
 */
public final class SystemInformationCollector {

  private static final Logger LOGGER = LoggerFactory.getLogger(SystemInformationCollector.class);
  private static final SystemInformationCollector INSTANCE = new SystemInformationCollector();

  private static final AtomicBoolean COLLECTED = new AtomicBoolean(false);

  private final SystemInfo systemInfo = new SystemInfo();
  private final List<SystemInformation> infos = new ArrayList<>();
  private final DecimalFormat decimalFormat = new DecimalFormat("0.#");

  private SystemInformationCollector() {
    decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
  }

  /**
   * 执行系统硬件信息收集
   * <p>
   * 按顺序收集各类硬件信息，捕获异常以确保不影响其他信息的收集。
   * 信息收集完成后标记为已收集状态，防止重复收集。
   */
  public static void collect() {
    if (!COLLECTED.compareAndSet(false, true)) {
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
  }

  /**
   * 获取所有已收集的系统硬件信息
   *
   * @return 不可变的系统信息列表
   */
  public static List<SystemInformation> getAllInformation() {
    return Collections.unmodifiableList(INSTANCE.infos);
  }

  /**
   * 收集操作系统信息
   * <p>
   * 包括进程ID、操作系统厂商、版本、位数等。
   */
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
   * 收集CPU信息
   * <p>
   * 包括物理/逻辑CPU数量、厂商、名称、架构、微代码、频率等。
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
   * 收集物理内存信息
   * <p>
   * 包括总内存大小、内存页大小、各物理内存条详细信息。
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
   * 收集磁盘存储信息
   * <p>
   * 包括磁盘名称、型号、序列号、总容量等。
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
   * 收集网络接口信息
   * <p>
   * 包括网卡名称、描述、IPv4/IPv6地址、MAC地址等。
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
    if (bytes == null) {
      return "0 GB";
    }
    double result = ((double) bytes) / 1024 / 1024 / 1024;
    return decimalFormat.format(result) + " GB";
  }

  private String calculateHZ(Long bytes) {
    if (bytes == null) {
      return "0 GHz";
    }
    double result = ((double) bytes) / 1000 / 1000 / 1000;
    return decimalFormat.format(result) + " GHz";
  }

  private String calculateMHZ(Long bytes) {
    if (bytes == null) {
      return "0 MHz";
    }
    double result = ((double) bytes) / 1000 / 1000;
    return decimalFormat.format(result) + " MHz";
  }
}
