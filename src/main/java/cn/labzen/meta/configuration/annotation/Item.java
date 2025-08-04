package cn.labzen.meta.configuration.annotation;

import org.slf4j.event.Level;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Item {

  String path() default "";

  boolean required() default true;

  Level logLevel() default Level.DEBUG;

  String defaultValue() default "";
}
