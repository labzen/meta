package cn.labzen.meta.configuration.bean;

import org.slf4j.event.Level;

import java.lang.reflect.Method;

public record Meta(Method method, Class<?> returnType, String path, boolean required, Level logLevel,
                   String defaultValue) {

}
