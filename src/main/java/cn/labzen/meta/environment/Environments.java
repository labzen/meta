package cn.labzen.meta.environment;

/**
 * @param javaVersion   运行时环境版本
 * @param javaVendor    运行时环境供应商
 * @param javaVendorUrl 运行时环境供应商网站
 * @param javaHome      运行时安装目录
 * @param classVersion  类格式版本号
 * @param classpath     Path
 * @param libraryPath   加载库时搜索的路径列表
 * @param pathSeparator 系统路径分隔符
 * @param fileSeparator 系统文件分隔符
 * @param lineSeparator 行分隔符
 * @param userHome      用户的主目录
 * @param userDir       用户的当前工作目录
 * @param ioTempPath    默认的临时文件路径
 */
public record Environments(String javaVersion, String javaVendor, String javaVendorUrl, String javaHome,
                           String classVersion, String classpath, String libraryPath, String pathSeparator,
                           String fileSeparator, String lineSeparator, String userHome, String userDir,
                           String ioTempPath) {

}
