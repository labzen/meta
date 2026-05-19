package cn.labzen.meta.spring;

/**
 * Spring初始化顺序常量定义
 * <p>
 * 定义不同模块使用spring.factories文件中ApplicationContextInitializer时的默认顺序。
 * 顺序值从Integer.MIN_VALUE开始递增，确保元数据模块最早初始化。
 * 注意：实际加载顺序可能因模块依赖关系而有所调整。
 */
public interface SpringInitializationOrder {

  int MODULE_META_INITIALIZER_ORDER = Integer.MIN_VALUE + 1_000;
  int MODULE_LOGGER_INITIALIZER_ORDER = Integer.MIN_VALUE + 2_000;
  int MODULE_SPRING_INITIALIZER_ORDER = Integer.MIN_VALUE + 3_000;
  int MODULE_WEB_INITIALIZER_ORDER = Integer.MIN_VALUE + 4_000;
  int MODULE_PLUGIN_INITIALIZER_ORDER = Integer.MIN_VALUE + 5_000;
  int MODULE_SQL_INITIALIZER_ORDER = Integer.MIN_VALUE + 6_000;
  int MODULE_FILE_INITIALIZER_ORDER = Integer.MIN_VALUE + 7_000;
  int MODULE_AUTHORITY_INITIALIZER_ORDER = Integer.MIN_VALUE + 8_000;
  int MODULE_CACHE_INITIALIZER_ORDER = Integer.MIN_VALUE + 9_000;
  int MODULE_MQ_INITIALIZER_ORDER = Integer.MIN_VALUE + 10_000;
  int MODULE_RIGHTS_INITIALIZER_ORDER = Integer.MIN_VALUE + 11_000;
  int MODULE_JAVAFX_INITIALIZER_ORDER = Integer.MIN_VALUE + 12_000;
  int MODULE_SWING_INITIALIZER_ORDER = Integer.MIN_VALUE + 13_000;
}
