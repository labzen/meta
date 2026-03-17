package cn.labzen.meta.configuration;

import cn.labzen.meta.configuration.bean.Meta;
import javassist.util.proxy.MethodHandler;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverterSupport;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public final class ConfigurationMethodHandler implements MethodHandler {

  private static final TypeConverterSupport CONVERTER = new SimpleTypeConverter();

  private final Class<?> configuredInterface;
  private final String namespace;
  private final Map<Method, Meta> metas;

  ConfigurationMethodHandler(Class<?> configuredInterface, String namespace, Map<Method, Meta> metas) {
    this.configuredInterface = configuredInterface;
    this.namespace = namespace;
    this.metas = metas;
  }

  @Override
  public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
    if ("toString".equals(thisMethod.getName())) {
      return "The proxy instance of configuration interface: " + configuredInterface.getName();
    }

    Meta meta = metas.get(thisMethod);
    if (meta == null) {
      throw new IllegalStateException("无法解析的配置类选项方法：" + thisMethod.getName());
    }

    String path = namespace + "." + meta.path();
    Class<?> returnType = meta.returnType();
    Object value;

    if (List.class.isAssignableFrom(returnType)) {
      value = readToList(meta, path);
    } else if (Map.class.isAssignableFrom(returnType)) {
      value = readToMap(meta, path);
    } else {
      value = readToObject(meta, path);
    }

    return value;
  }

  private Object readToObject(Meta meta, String path) {
    Object object = ConfigurationProperties.get(path);
    if (object == null && meta.required()) {
      throw new IllegalStateException("配置项[" + path + "]不能为空");
    }
    if (object == null) {
      object = meta.defaultValue();
    }

    Class<?> returnType = meta.returnType();
    Object value = convertType(path, object, returnType);
    if (returnType.isPrimitive() && value == null) {
      // 若返回类型是基本类型且没有值（也没有默认值），提前抛出更明确的异常
      throw new IllegalStateException(String.format("配置项[%s]缺失，且未设置默认值，无法注入到基本类型[%s]",
          path,
          returnType.getName()));
    }
    return value;
  }

  private List<?> readToList(Meta meta, String basePath) {
    Type genericReturnType = meta.method().getGenericReturnType();
    Class<?> itemClass = getGenericType(genericReturnType);
    if (itemClass == null) {
      throw new IllegalArgumentException("不支持的返回类型：" + genericReturnType);
    }
    List<Object> list = new ArrayList<>();
    int index = 0;
    while (true) {
      String fullPath = basePath + "." + index;
      Object item = ConfigurationProperties.get(fullPath);
      if (item == null) {
        break;
      }
      item = convertType(fullPath, item, itemClass);
      list.add(item);
      index++;
    }

    if (list.isEmpty() && meta.required()) {
      throw new IllegalStateException("配置项[" + basePath + "]不能为空");
    } else if (list.isEmpty()) {
      String defaultValue = meta.defaultValue();
      list = Arrays.stream(defaultValue.split(",")).map(v -> convertType(basePath, v, itemClass)).toList();
    } else {
      list = List.copyOf(list);
    }

    return list;
  }

  private Map<?, ?> readToMap(Meta meta, String basePath) {
    Type genericReturnType = meta.method().getGenericReturnType();
    Class<?> itemClass = getGenericType(genericReturnType);

    Set<String> keys = ConfigurationProperties.keys();
    String prefix = basePath + ".";
    Map<String, Object> map = keys.stream()
                                  .filter(k -> k.startsWith(prefix))
                                  .collect(Collectors.toUnmodifiableMap(k -> k.substring(prefix.length()), k -> {
                                    Object object = ConfigurationProperties.get(k);
                                    return convertType(k, object, itemClass);
                                  }));

    if (map.isEmpty() && meta.required()) {
      throw new IllegalStateException("配置项[" + basePath + "]不能为空");
    } else if (map.isEmpty()) {
      String defaultValue = meta.defaultValue();
      map = Arrays.stream(defaultValue.split(","))
                  .map(o -> o.split("="))
                  .filter(chips -> chips.length == 2)
                  .collect(Collectors.toUnmodifiableMap(chips -> chips[0], chips -> chips[1]));
    }

    return map;
  }

  private Class<?> getGenericType(Type type) {
    if (!(type instanceof ParameterizedType parameterizedType)) {
      throw new IllegalStateException();
    }

    // 获取原始类型
    //Type rawType = parameterizedType.getRawType();

    // 获取实际类型参数
    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
    Type typeArg = actualTypeArguments[actualTypeArguments.length - 1];
    return toClass(typeArg);
  }

  private Class<?> toClass(Type type) {
    if (type instanceof Class) {
      return (Class<?>) type;
    }

    // 如果是ParameterizedType（泛型类型）
    if (type instanceof ParameterizedType parameterizedType) {
      Type rawType = parameterizedType.getRawType();
      return toClass(rawType);
    }

    // 如果是TypeVariable（类型变量，如 T, E, K, V）
    if (type instanceof TypeVariable) {
      throw new IllegalArgumentException("不支持泛型变量");
    }

    // 如果是WildcardType（通配符类型，如 ? extends Number）
    if (type instanceof WildcardType wildcardType) {
      Type[] upperBounds = wildcardType.getUpperBounds();
      if (upperBounds.length > 0) {
        return toClass(upperBounds[0]);
      }
      Type[] lowerBounds = wildcardType.getLowerBounds();
      if (lowerBounds.length > 0) {
        return toClass(lowerBounds[0]);
      }
      return Object.class;
    }

    // 如果是GenericArrayType（泛型数组类型）
    if (type instanceof GenericArrayType genericArrayType) {
      Type componentType = genericArrayType.getGenericComponentType();
      Class<?> componentClass = toClass(componentType);
      if (componentClass != null) {
        // 创建数组类型
        return Array.newInstance(componentClass, 0).getClass();
      }
    }

    return null;
  }

  private Object convertType(String path, Object value, Class<?> type) {
    try {
      return CONVERTER.convertIfNecessary(value, type);
    } catch (Exception ex) {
      throw new IllegalStateException(String.format("配置项[%s]的值[%s]无法转换为类型[%s]",
          path,
          value,
          type.getName()), ex);
    }
  }
}
