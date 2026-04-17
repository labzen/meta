package cn.labzen.meta.component.bean;

/**
 * 组件基本信息
 * <p>
 * 记录组件的标识信息，包括标题、供应商、版本号和描述。
 * 从JAR Manifest文件、pom.xml或Package元数据中提取。
 *
 * @param title       组件标题/名称
 * @param vendor      供应商/开发者
 * @param version     版本号
 * @param description 组件描述
 */
public record Information(String title, String vendor, String version, String description) {

}
