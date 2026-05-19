package cn.labzen.meta.configuration.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * YAML配置文件解析器
 * <p>
 * 实现ConfigurationFileResolver接口，负责从classpath中查找并解析YAML配置文件。
 * 支持的配置文件路径：labzen.yml、labzen.yaml、META-INF/labzen.yml、META-INF/labzen.yaml。
 * 解析后的配置项以点分隔的路径形式存储，支持嵌套结构展开。
 */
public class YamlConfigurationFileResolver implements ConfigurationFileResolver {

  private final Logger logger = LoggerFactory.getLogger(YamlConfigurationFileResolver.class);

  private static final List<String> CONFIG_FILE_LOCATIONS = List.of("classpath:labzen.yml",
      "classpath:labzen.yaml",
      "classpath:META-INF/labzen.yml",
      "classpath:META-INF/labzen.yaml");

  /**
   * 解析YAML配置文件
   * <p>
   * 按优先级查找配置文件，找到第一个存在的文件后读取并解析为Map结构。
   *
   * @return 配置项Map，键为点分隔路径，值为配置值。若无配置文件则返回空Map。
   */
  @Override
  public Map<String, Object> resolve() {
    PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
    Resource resource = null;

    for (String location : CONFIG_FILE_LOCATIONS) {
      resource = pathResolver.getResource(location);
      if (resource.exists()) {
        logger.info("加载Labzen配置文件： {}", resource.getFilename());
        break;
      }
      resource = null;
    }

    if (resource == null) {
      return Map.of();
    }

    try (InputStream inputStream = resource.getInputStream()) {
      return loadYamlAsMap(inputStream);
    } catch (IOException e) {
      logger.error("无法读取配置文件", e);
      return Map.of();
    } catch (Exception e) {
      logger.error("配置文件格式错误或解析失败", e);
      return Map.of();
    }
  }

  private Map<String, Object> loadYamlAsMap(InputStream inputStream) {
    LoaderOptions options = new LoaderOptions();
    options.setAllowDuplicateKeys(false);
    options.setAllowDuplicateKeys(true);
    Yaml yaml = new Yaml(new SafeConstructor(options));
    Object loaded = yaml.load(inputStream);
    Map<String, Object> loadedMap = asMap(loaded);

    Map<String, Object> result = new HashMap<>();
    buildFlattenedMap(result, loadedMap, null);
    return result;
  }

  /**
   * 将YAML对象转换为Map结构
   * <p>
   * 递归处理嵌套的Map和Collection类型。
   */
  private Map<String, Object> asMap(Object object) {
    // YAML can have numbers as keys
    Map<String, Object> result = new LinkedHashMap<>();
    if (!(object instanceof Map)) {
      // A document can be a text literal
      result.put("document", object);
      return result;
    }

    Map<?, ?> om = (Map<?, ?>) object;
    om.forEach((key, value) -> {
      if (value instanceof Map) {
        result.put(key.toString(), asMap(value));
      } else {
        result.put(key.toString(), value);
      }
    });
    return result;
  }

  /**
   * 构建扁平化的配置Map
   * <p>
   * 将嵌套的YAML结构展开为点分隔路径的形式。
   * List类型展开为索引形式（path.0, path.1），Map类型递归展开。
   */
  @SuppressWarnings("unchecked")
  private void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, String path) {
    source.forEach((key, value) -> {
      String p = key;
      if (path != null && !path.isEmpty()) {
        p = path + "." + key;
      }

      switch (value) {
        case String s -> result.put(p, value);
        case Map<?, ?> map -> buildFlattenedMap(result, (Map<String, Object>) value, p);
        case Collection<?> collection -> {
          Collection<Object> coll = (Collection<Object>) value;
          if (coll.isEmpty()) {
            result.put(p, "");
          } else {
            int index = 0;
            for (Object ele : coll) {
              buildFlattenedMap(result, Collections.singletonMap(String.valueOf(index++), ele), p);
            }
          }
        }
        case null, default -> result.put(p, Objects.requireNonNullElse(value, ""));
      }
    });
  }
}
