package cn.labzen.meta.spring;

/**
 * 定义不同模块使用 spring.factories 文件中定义 ApplicationContextInitializer 时的默认顺序
 * 顺序仅供参考，后续模块的实际加载顺序可能有变
 * todo delete
 */
public interface SpringInitializationOrder {

  int MODULE_META_INITIALIZER_ORDER = Integer.MIN_VALUE + 1_000;
  int MODULE_LOGGER_INITIALIZER_ORDER = Integer.MIN_VALUE + 2_000;
  int MODULE_SPRING_INITIALIZER_ORDER = Integer.MIN_VALUE + 3_000;
  int MODULE_CORE_INITIALIZER_ORDER = Integer.MIN_VALUE + 4_000;
  int MODULE_PLUGIN_INITIALIZER_ORDER = Integer.MIN_VALUE + 5_000;
  int MODULE_SQL_INITIALIZER_ORDER = Integer.MIN_VALUE + 6_000;
  int MODULE_WEB_INITIALIZER_ORDER = Integer.MIN_VALUE + 7_000;
  int MODULE_AUTHORITY_INITIALIZER_ORDER = Integer.MIN_VALUE + 8_000;
  int MODULE_CACHE_INITIALIZER_ORDER = Integer.MIN_VALUE + 9_000;
  int MODULE_MQ_INITIALIZER_ORDER = Integer.MIN_VALUE + 10_000;
  int MODULE_RIGHTS_INITIALIZER_ORDER = Integer.MIN_VALUE + 11_000;
  int MODULE_JAVAFX_INITIALIZER_ORDER = Integer.MIN_VALUE + 12_000;
  int MODULE_SWING_INITIALIZER_ORDER = Integer.MIN_VALUE + 12_000;
}
