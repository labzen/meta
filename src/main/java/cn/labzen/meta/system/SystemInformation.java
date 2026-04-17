package cn.labzen.meta.system;

/**
 * 系统硬件信息数据对象
 * <p>
 * 封装单个系统硬件信息的目录、名称、显示标题和描述。
 * 使用不可变record类型保证线程安全。
 *
 * @param catalog     信息所属分类，如"hardware.processor"
 * @param name       信息名称（键）
 * @param title      友好的显示标题
 * @param description 详细描述/值
 */
public record SystemInformation(String catalog, String name, String title, String description) {

}
