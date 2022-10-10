package cn.labzen.meta;

import cn.labzen.meta.configuration.annotation.Configured;
import cn.labzen.meta.configuration.annotation.Item;

@Configured(namespace = "meta")
public interface MetaConfiguration {

  String test();

  @Item(defaultValue = "debug")
  String defaultLogLevel();

  @Item(path = "sub.fun")
  String subFun();

  @Item(defaultValue = "44")
  int number();

  @Item(path = "double", defaultValue = "99.88")
  double doubleNumber();

  @Item
  boolean bool();
}
