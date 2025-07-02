---
jupyter:
  jupytext:
    text_representation:
      extension: .md
      format_name: markdown
      format_version: '1.3'
      jupytext_version: 1.14.4
  kernelspec:
    display_name: C++
    language: c++
    name: xcpp11
---

---
Created time: 2025-04-01T15:46
加入带背: true
jupyter:
  jupytext:
    cell_metadata_filter: language,-all
    formats: md,ipynb
    main_language: c++
    text_representation:
      extension: .md
      format_name: markdown
      format_version: '1.3'
      jupytext_version: 1.14.7
---
很好！这个问题是 C/C++ 中的基础类型问题，必须搞清楚。
---
## 🎯 什么是 `size_t`？
`size_t` 是 C/C++ 标准库定义的 **无符号整数类型**，**专门用于表示**：
- **内存大小**
- **容器大小**
- **数组下标**
- **对象大小**
---
## 📄 定义
在 C++ 中，它通常是这样定义的：


```
typedef unsigned long size_t;    // 64位系统
typedef unsigned int size_t;     // 32位系统
```

也就是说，它本质上就是 **平台相关的无符号整数类型**。
|   |   |
|---|---|
|平台|size_t 大小|
|32位|4字节 (`unsigned int`)|
|64位|8字节 (`unsigned long`)|
---
## ✅ 主要用途
|   |   |
|---|---|
|用途|示例|
|**表示内存或容器大小**|`std::vector<int>::size()` 返回 `size_t`|
|**表示数组下标**|`for (size_t i = 0; i < n; ++i)`|
|**表示字节数**|`sizeof()` 运算符返回 `size_t`|
|**内存操作**|`memset(ptr, 0, sizeof(obj));` 中的 `sizeof` 是 `size_t`|
---
## 🔥 例子


```
\#include <iostream>
\#include <vector>
int main() {
    size_t n = 10; // 无符号整数
    std::vector<int> v(n);
    std::cout << "Vector size: " << v.size() << "\n";  // 返回值类型是 size_t
}
```

---
## 🚨 注意事项
|   |   |
|---|---|
|注意|说明|
|**是无符号类型**|不能用于和负数比较，避免 `i >= 0` 等错误|
|**跨平台大小不同**|32位系统是 4字节，64位是 8字节，但写代码时无需关心|
|**和** `**int**` **比较需小心**|避免 signed/unsigned mismatch 警告|
---
## 📝 学习笔记模板
```Plain
# size_t 笔记
✅ 定义：
- 无符号整数类型
- 用于表示对象或容器的大小
📌 来源：
typedef unsigned long size_t;
🎯 用途：
- sizeof 操作符返回值
- vector.size()、string.length() 返回值
- 内存操作参数 (如 memset, memcpy)
⚠️ 注意：
- 是无符号类型 (>=0)
- 不要与 int 混用比较
```
---
如果你想，我可以帮你整理一套 **size_t 常见陷阱 & 面试题**，帮你快速掌握它在实际项目中的注意事项。要不要？
很好！这个问题是 C/C++ 中的基础类型问题，必须搞清楚。
---
## 🎯 什么是 `size_t`？
`size_t` 是 C/C++ 标准库定义的 **无符号整数类型**，**专门用于表示**：
- **内存大小**
- **容器大小**
- **数组下标**
- **对象大小**
---
## 📄 定义
在 C++ 中，它通常是这样定义的：


```
typedef unsigned long size_t;    // 64位系统
typedef unsigned int size_t;     // 32位系统
```

也就是说，它本质上就是 **平台相关的无符号整数类型**。
|   |   |
|---|---|
|平台|size_t 大小|
|32位|4字节 (`unsigned int`)|
|64位|8字节 (`unsigned long`)|
---
## ✅ 主要用途
|   |   |
|---|---|
|用途|示例|
|**表示内存或容器大小**|`std::vector<int>::size()` 返回 `size_t`|
|**表示数组下标**|`for (size_t i = 0; i < n; ++i)`|
|**表示字节数**|`sizeof()` 运算符返回 `size_t`|
|**内存操作**|`memset(ptr, 0, sizeof(obj));` 中的 `sizeof` 是 `size_t`|
---
## 🔥 例子


```
\#include <iostream>
\#include <vector>
int main() {
    size_t n = 10; // 无符号整数
    std::vector<int> v(n);
    std::cout << "Vector size: " << v.size() << "\n";  // 返回值类型是 size_t
}
```

---
## 🚨 注意事项
|   |   |
|---|---|
|注意|说明|
|**是无符号类型**|不能用于和负数比较，避免 `i >= 0` 等错误|
|**跨平台大小不同**|32位系统是 4字节，64位是 8字节，但写代码时无需关心|
|**和** `**int**` **比较需小心**|避免 signed/unsigned mismatch 警告|
---
## 📝 学习笔记模板
```Plain
# size_t 笔记
✅ 定义：
- 无符号整数类型
- 用于表示对象或容器的大小
📌 来源：
typedef unsigned long size_t;
🎯 用途：
- sizeof 操作符返回值
- vector.size()、string.length() 返回值
- 内存操作参数 (如 memset, memcpy)
⚠️ 注意：
- 是无符号类型 (>=0)
- 不要与 int 混用比较
```
---
如果你想，我可以帮你整理一套 **size_t 常见陷阱 & 面试题**，帮你快速掌握它在实际项目中的注意事项。要不要？
