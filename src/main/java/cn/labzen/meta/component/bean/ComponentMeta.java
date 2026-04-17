package cn.labzen.meta.component.bean;

import cn.labzen.meta.component.DeclaredComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 组件元数据
 * <p>
 * 记录组件的基本信息和对应的组件实现类。
 * 通过ServiceLoader加载后注册到全局缓存中。
 *
 * @param information 组件基本信息（标题、供应商、版本、描述）
 * @param component  组件实现类
 */
public record ComponentMeta(@Nullable Information information, @Nonnull DeclaredComponent component) {

}
