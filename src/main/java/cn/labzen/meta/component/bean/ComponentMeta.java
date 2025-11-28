package cn.labzen.meta.component.bean;

import cn.labzen.meta.component.DeclaredComponent;

import javax.annotation.Nonnull;

public record ComponentMeta(@Nonnull Information information, @Nonnull DeclaredComponent component) {

}
