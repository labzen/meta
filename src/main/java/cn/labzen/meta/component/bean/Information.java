package cn.labzen.meta.component.bean;

import javax.annotation.Nonnull;

public record Information(@Nonnull String title, String vendor, String version, String description) {

}
