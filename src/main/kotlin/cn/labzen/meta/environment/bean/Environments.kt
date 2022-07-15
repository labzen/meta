package cn.labzen.meta.environment.bean

/**
 * @param javaVersion 运行时环境版本
 * @param javaVendor 运行时环境供应商
 * @param javaVendorVersion 运行时环境供应商版本
 * @param javaHome 运行时安装目录
 * @param classVersion 类格式版本号
 * @param classpath Class Path
 * @param libraryPath 加载库时搜索的路径列表
 * @param timezone 时区
 * @param userHome 用户的主目录
 * @param userDir 用户的当前工作目录
 * @param userLanguage 用户使用语言
 * @param ioTempPath 默认的临时文件路径
 */
data class Environments internal constructor(
  val javaVersion: String,
  val javaVendor: String,
  val javaVendorVersion: String,
  val javaHome: String,
  val classVersion: String,
  val classpath: String,
  val libraryPath: String,
  val timezone: String,
  val userHome: String,
  val userDir: String,
  val userLanguage: String,
  val ioTempPath: String,
)
