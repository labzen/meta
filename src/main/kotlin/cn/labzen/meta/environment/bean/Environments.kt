package cn.labzen.meta.environment.bean

/**
 * @param javaVersion 运行时环境版本
 * @param javaVendor 运行时环境供应商
 * @param javaVendorUrl 运行时环境供应商网站
 * @param javaHome 运行时安装目录
 * @param classVersion 类格式版本号
 * @param classpath Class Path
 * @param libraryPath 加载库时搜索的路径列表
 * @param pathSeparator 系统路径分隔符
 * @param fileSeparator 系统文件分隔符
 * @param lineSeparator 行分隔符
 * @param userHome 用户的主目录
 * @param userDir 用户的当前工作目录
 * @param ioTempPath 默认的临时文件路径
 */
data class Environments internal constructor(
  val javaVersion: String,
  val javaVendor: String,
  val javaVendorUrl: String,
  val javaHome: String,
  val classVersion: String,
  val classpath: String,
  val libraryPath: String,
  val pathSeparator: String,
  val fileSeparator: String,
  val lineSeparator: String,
  val userHome: String,
  val userDir: String,
  val ioTempPath: String,
)
