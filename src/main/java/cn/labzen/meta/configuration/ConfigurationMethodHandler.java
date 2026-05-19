package cn.labzen.meta.configuration;

import cn.labzen.meta.configuration.bean.Meta;
import javassist.util.proxy.MethodHandler;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverterSupport;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 配置方法调用处理器
 * <p>
 * 作为Javassist动态代理的MethodHandler，拦截配置接口的所有方法调用。
 * 根据方法元数据从配置属性Map中读取值，支持基本类型、List、Map等复杂类型的自动转换。
 *
 * @see javassist.util.proxy.MethodHandler
 */
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

  /**
   * 拦截配置接口的方法调用
   * <p>
   * 根据调用方法确定配置路径，从全局配置中读取值并进行类型转换。
   * 支持toString、hashCode、equals等Object方法的特殊处理。
   *
   * @param self       代理对象本身
   * @param thisMethod 被调用的方法
   * @param proceed    proceed参数在此实现中未使用
   * @param args       方法参数
   * @return 方法返回值
   * @throws Throwable 若配置读取或类型转换失败
   */
  @Override
  public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
    String methodName = thisMethod.getName();
    // toString方法返回代理实例的描述信息
    if ("toString".equals(methodName)) {
      return "The proxy instance of configuration interface: " + configuredInterface.getName();
    }
    // hashCode和equals方法不支持，避免代理对象被误用
    if ("hashCode".equals(methodName) || "equals".equals(methodName)) {
      throw new UnsupportedOperationException("不支持的配置类选项方法：" + methodName);
    }

    // 根据方法获取对应的配置元数据
    Meta meta = metas.get(thisMethod);
    if (meta == null) {
      throw new IllegalStateException("无法解析的配置类选项方法：" + methodName);
    }

    // 构建完整配置路径：命名空间 + 方法配置路径
    String path = namespace + "." + meta.path();
    Class<?> returnType = meta.returnType();
    Object value;

    // 根据返回类型选择不同的读取策略
    if (List.class.isAssignableFrom(returnType)) {
      value = readToList(meta, path);
    } else if (Map.class.isAssignableFrom(returnType)) {
      value = readToMap(meta, path);
    } else {
      value = readToObject(meta, path);
    }

    return value;
  }

  /**
   * 读取单个配置值
   * <p>
   * 从配置属性Map中获取值，检查必填约束和应用默认值，最终将值转换为方法声明的返回类型。
   */
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

  /**
   * 读取List类型的配置值
   * <p>
   * 从配置属性Map中读取索引递增的配置项（path.0, path.1, ...），
   * 将每一项转换为List元素类型。若配置为空则使用默认值或返回空List。
   *
   * @param meta     配置元数据
   * @param basePath 配置路径前缀
   * @return 配置值列表
   */
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
      if (defaultValue != null) {
        list = Arrays.stream(defaultValue.split(",")).map(v -> convertType(basePath, v, itemClass)).toList();
      } else {
        list = List.of();
      }
    } else {
      list = List.copyOf(list);
    }

    return list;
  }

  /**
   * 读取Map类型的配置值
   * <p>
   * 从配置属性Map中筛选出以path.为前缀的项作为Map的键值对，
   * 将每个值转换为Map元素类型。若配置为空则使用默认值或返回空Map。
   *
   * @param meta     配置元数据
   * @param basePath 配置路径前缀
   * @return 配置值Map
   */
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
      if (defaultValue != null) {
        map = Arrays.stream(defaultValue.split(","))
                    .map(o -> o.split("="))
                    .filter(chips -> chips.length == 2)
                    .collect(Collectors.toUnmodifiableMap(chips -> chips[0],
                        chips -> convertType(basePath, chips[1], itemClass)));
      } else {
        map = Map.of();
      }
    }

    return map;
  }

  /**
   * 从泛型类型中提取元素类型
   * <p>
   * 支持处理ParameterizedType、TypeVariable、WildcardType、GenericArrayType等泛型类型。
   *
   * @param type 泛型类型
   * @return 解析后的Class类型
   */
  private Class<?> getGenericType(Type type) {
    if (!(type instanceof ParameterizedType parameterizedType)) {
      throw new IllegalStateException("配置方法的返回类型必须带有泛型参数，当前类型: " +
                                      type +
                                      "，请使用如 List<String> 而非裸 List");
    }

    // 获取原始类型
    //Type rawType = parameterizedType.getRawType();

    // 获取实际类型参数
    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
    Type typeArg = actualTypeArguments[actualTypeArguments.length - 1];
    return toClass(typeArg);
  }

  /**
   * 将Type转换为Class
   * <p>
   * 递归处理各种Type类型，返回最终的Class对象。
   *
   * @param type Java类型
   * @return 对应的Class对象，若无法转换则返回null
   */
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

  /**
   * 类型转换
   * <p>
   * 使用Spring的SimpleTypeConverter将配置值转换为目标类型。
   */
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
