package cn.labzen.meta.configuration.resolver;

import java.util.Map;

public interface ConfigurationFileResolver {

  Map<String, Object> resolve();
}
