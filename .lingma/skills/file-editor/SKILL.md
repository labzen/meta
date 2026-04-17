---
name: file-editor
description: 编辑和创建项目文件。用于修改代码文件、添加注释、创建新文件等。当用户要求修改代码、添加注释、创建文件或编辑项目文件时使用。
---

# 文件编辑技能

## 核心能力

当用户要求编辑文件时，直接修改目标文件的内容。使用 CODE_EDIT_BLOCK 格式输出修改后的代码块。

## 文件读取

1. 使用 `read_file` 工具读取完整文件内容
2. 如果文件较大，使用 `start_line` 和 `end_line` 参数分段读取
3. 读取后分析代码结构和需要修改的位置

## CODE_EDIT_BLOCK 输出格式

// ... existing code ...
[修改的代码]
// ... existing code ...

### 格式说明

- **语言**：文件对应的编程语言（java、python、js 等）
- **文件绝对路径**：使用反斜杠的 Windows 路径
- `// ... existing code ...`：表示未修改的代码上下文，必须保留
- 删除代码使用 `// Deleted:` 前缀

## 注释添加规范

### 类级注释

在类声明上方添加 Javadoc 风格的注释：

```java
/**
 * 类的功能描述
 * <p/>
 * 详细说明类的用途、职责和使用方式
 * @see 相关类
 **/
public class MyClass { }
```

### 方法级注释

在方法上方添加 Javadoc 注释：

```java
/**
 * 方法功能描述
 *
 * @param paramName 参数说明
 * @throws Exception 异常说明
 * @return 返回值说明
 **/
public String methodName(String paramName) throws Exception { }
```

### 行内注释

在关键逻辑处添加单行注释：
```java
// 校验输入参数非空
if (input == null) {
  throw new IllegalArgumentException();
}
```

## 修改示例

### 示例1：添加类注释
```java
/**
 * 应用程序主入口类
 *
 * 负责初始化Spring容器并启动应用
 *
 * @see SpringApplication */
public class Main {
// ... existing code ...
```

### 示例2：添加方法注释和行内注释

```java
// ... existing code ...
/**
 * 处理用户请求
 *
 * @param request 用户请求对象
 * @return 处理结果
 **/
public Result process(Request request) {
  // 步骤1：参数校验 validate(request);
  // 步骤2：执行业务逻辑
  Object data = execute(request);

  // 步骤3：封装返回结果
  return new Result(data);
}
// ... existing code ...
```

### 示例3：修改代码逻辑

```java
// ... existing code ...
// 添加空值检查逻辑
if (value == null) {
  logger.warn("检测到空值，使用默认值");
  value = defaultValue;
}
// ... existing code ...
```

### 示例4：删除代码

```java
// ... existing code ...
// Deleted: // 已废弃的方法，迁移到新实现
// Deleted: public void oldMethod() {
// Deleted: System.out.println("deprecated");
// Deleted: }
// ... existing code ...
```

## 关键规则

1. **只输出修改的代码块**：不需要输出整个文件
2. **保留上下文标记**：必须包含 `// ... existing code ...`
3. **语言标识匹配**：使用正确的文件扩展名
4. **中文注释**：使用中文编写注释，简明扼要
5. **中文标点**：使用中文标点符号（，。：；？！""）
6. **不添加无意义注释**：注释应解释"为什么"，而非"是什么"

## 修改流程

1. 接收用户修改需求
2. 使用 `read_file` 读取原文件
3. 分析需要修改的位置和内容
4. 按上述格式输出 CODE_EDIT_BLOCK
5. 用户确认后执行实际修改
