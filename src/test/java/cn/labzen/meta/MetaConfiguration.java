package cn.labzen.meta;

import cn.labzen.meta.configuration.annotation.Configured;
import cn.labzen.meta.configuration.annotation.Item;

@Configured(namespace = "meta")
public interface MetaConfiguration {

  String test();

  @Item(defaultValue = "debug")
  String defaultLogLevel();
}
