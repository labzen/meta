package cn.labzen.meta.configuration.annotation;

import org.slf4j.event.Level;

import java.lang.annotation.*;

/**
 * 配置项注解
 * <p>
 * 标注在配置接口的方法上，用于指定配置项的读取规则。
 * 可指定配置路径、是否必填、日志级别和默认值。
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Item {

  String path() default "";

  boolean required() default true;

  Level logLevel() default Level.DEBUG;

  String defaultValue() default "";
}
