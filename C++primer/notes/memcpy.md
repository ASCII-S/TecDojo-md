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
Created time: 2025-04-01T15:56
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
非常好！你问到的 `memcpy` 是 **C/C++ 标准库中最常用的内存操作函数之一**，和 `memset` 一样，是底层程序开发的必备工具。
---
## 🎯 什么是 `memcpy`？

> memcpy 是用于将一段内存的内容 “原样复制” 到另一段内存 的函数。
它 **不会管数据类型，只管内存块的字节复制**。
---
## 📄 函数原型


```
void* memcpy(void* dest, const void* src, size_t n);
```

|   |   |
|---|---|
|参数|说明|
|`dest`|目标内存地址（复制到这里）|
|`src`|源内存地址（从这里复制）|
|`n`|复制的字节数|
---
## ✅ 典型用法
### 1. 基本示例


```
\#include <cstring>
\#include <iostream>
int main() {
    int a[5] = {1, 2, 3, 4, 5};
    int b[5];
    memcpy(b, a, sizeof(a));  // 将 a 的内容复制到 b
    for (int i = 0; i < 5; ++i) {
        std::cout << b[i] << " ";  // 输出: 1 2 3 4 5
    }
}
```

---
### 2. 用在结构体拷贝


```
struct Node {
    int id;
    double weight;
};
Node n1 = {1, 2.5};
Node n2;
memcpy(&n2, &n1, sizeof(Node));  // 原样复制
```

---
## 🚀 注意事项
|   |   |
|---|---|
|⚠️ 事项|说明|
|**只复制字节**|不做类型检查，不调用构造/析构函数|
|**适用于 POD 类型**|只适用于 **Plain Old Data (POD)** 类型，如 int、struct，不适用于包含虚函数、指针的复杂类型|
|**可能会造成浅拷贝问题**|如果结构体内部有指针，`memcpy` 只复制指针地址，导致资源共享或重复释放问题|
|**源与目标地址不能重叠**|如果有重叠，请使用 `memmove()`|
---
## 📝 学习笔记模板
```Plain
# memcpy 快速笔记
✅ 定义：
将 n 个字节从 src 内存复制到 dest 内存
📌 函数原型：
void* memcpy(void* dest, const void* src, size_t n);
🎯 典型用途：
- 数组/结构体二进制拷贝
- 文件读取时二进制数据转结构体
- 大块内存搬运
⚠️ 注意事项：
- 只适用于 POD 类型
- 不做类型检查
- 目标与源内存 **不能重叠** (用 memmove)
```
---
## 🌟 补充：`memcpy` vs `std::copy`
|   |   |   |   |
|---|---|---|---|
|函数|类型安全|适用类型|是否调用构造函数|
|`memcpy`|❌ 无类型安全|POD 类型|❌ 不调用|
|`std::copy`|✅ 类型安全|任意类型|✅ 调用拷贝构造|
---
如果你想，我可以帮你整理一套 **memcpy 常见陷阱 & 实际案例**，教你怎么避免踩坑。要不要？
好主意！`memcpy` 很强，但 **非常容易踩坑**。下面帮你整理一套 **memcpy 常见陷阱 & 实际案例**，你可以直接收藏作为开发避坑手册。
---
# 🚨 memcpy 常见陷阱 & 实际案例
---
## 🧩 陷阱 1 —— 用在 **非 POD 类型**
### ❗ 问题
`memcpy` **只适用于 POD 类型**（Plain Old Data），如果你拿它去复制 **C++ 类对象**，会导致 **构造函数、析构函数**不被调用，出现 **资源泄漏** 或 **重复释放**。
### ❌ 错误示例


```
\#include <cstring>
\#include <string>
std::string s1 = "Hello";
std::string s2;
memcpy(&s2, &s1, sizeof(s1));  // ❗️浅拷贝，s1 和 s2 内部共享同一个 char*，析构时重复释放
```

---
## 🧩 陷阱 2 —— 内存重叠时使用 memcpy
### ❗ 问题
`memcpy` **源地址和目标地址 不能重叠**，否则结果是 **未定义行为**。
### ❌ 错误示例


```
char buffer[] = "abcdef";
memcpy(buffer + 2, buffer, 4);  // ❗️重叠区域
```

### ✅ 正确做法
使用 `**memmove**`：


```
memmove(buffer + 2, buffer, 4);
```

---
## 🧩 陷阱 3 —— 忽略字节数
### ❗ 问题
很多初学者会忘记乘上 `sizeof(T)`，导致 **只复制一部分数据**。
### ❌ 错误示例


```
int a[5] = {1, 2, 3, 4, 5};
int b[5];
memcpy(b, a, 5);  // ❗️错误，只复制5个字节，不是5个整数
```

### ✅ 正确做法


```
memcpy(b, a, 5 * sizeof(int));
```

---
## 🧩 陷阱 4 —— 复制结构体含有指针成员
### ❗ 问题
如果结构体内部有 **指针成员**，`memcpy` 只会 **复制指针地址**，不会复制指针指向的数据，导致 **浅拷贝**问题。
### ❌ 错误示例


```
struct Node {
    int* data;
};
Node n1;
n1.data = new int(5);
Node n2;
memcpy(&n2, &n1, sizeof(Node));  // ❗️浅拷贝，n1 和 n2 共用同一个 int*
```

**后果：**
- 两个结构体析构时会重复释放同一块内存 → **程序崩溃**
---
## 🧩 陷阱 5 —— 忘记考虑字节对齐 (Alignment)
如果你在 **跨平台** 或 **结构体中有不同类型成员**，直接 `memcpy` 可能会因 **字节对齐** 带来隐患（尤其是在网络通信、二进制文件读写中）。
---
# 🎯 实际推荐场景
|   |   |
|---|---|
|使用场景|推荐操作|
|**大块内存初始化**|✅ `memset()` / `memcpy()`|
|**POD 类型数组复制**|✅ `memcpy()`|
|**结构体二进制 I/O**|✅ 先确认结构体是 POD 类型，且没有指针成员|
|**复杂对象复制**|❌ 不要用 memcpy，使用拷贝构造函数 / `std::copy()`|
---
# 📝 快速避坑手册
```Plain
# memcpy 常见陷阱
🚫 不要用在：
- 类对象 (有构造/析构函数)
- 内部含指针的结构体
- 内存区域重叠（用 memmove）
- 忽略 sizeof(T)
- 不考虑字节对齐
🎯 安全使用场景：
- POD 类型数组、结构体的原始复制
- 二进制文件读写
- 内存缓冲区搬运
```
---
如果你想，我可以帮你整理一套 **memcpy 与安全替代方案（如 std::copy, std::memmove, 拷贝构造）** 的对照表，方便你以后选型。要不要？
