package cn.labzen.meta.component.bean

data class Information(val title: String, val vendor: String, val version: String) {

  var description: String = ""
    internal set
}
