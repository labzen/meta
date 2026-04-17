package cn.labzen.meta.configuration.bean;

import org.slf4j.event.Level;

import java.lang.reflect.Method;

/**
 * 配置方法元数据
 * <p>
 * 封装配置接口方法的解析结果，包括方法引用、返回类型、配置路径、是否必填、日志级别和默认值。
 *
 * @param method       配置方法
 * @param returnType   返回类型
 * @param path         配置路径（相对于命名空间）
 * @param required     是否必填
 * @param logLevel     日志级别
 * @param defaultValue  默认值
 */
public record Meta(Method method, Class<?> returnType, String path, boolean required, Level logLevel,
                   String defaultValue) {

}
