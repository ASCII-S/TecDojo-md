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
Created time: 2025-04-02T16:05
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
好主意！这里帮你整理一套 `**std::unique_ptr**` **+** `**std::make_unique**` **的常见用法 & 注意事项**，让你系统掌握这个现代 C++的核心工具 👇
---
# 🚀 unique_ptr + make_unique 使用手册
---
## 🎯 1. 基本使用
### 创建 unique_ptr


```
auto p1 = std::make_unique<int>(10);  // 推荐
std::unique_ptr<int> p2(new int(20)); // 不推荐 (手动 new)
```

### 访问对象


```
*p1 = 30;
std::cout << *p1 << "\n";
```

---
## 🧩 2. 转移所有权 (Move)
`unique_ptr` 是 **独占所有权**，**不允许拷贝**，只能 **move**。


```
std::unique_ptr<int> p1 = std::make_unique<int>(10);
std::unique_ptr<int> p2 = std::move(p1);  // OK
// std::unique_ptr<int> p3 = p2;         // ❌ 错误，不能拷贝
```

---
## 🌈 3. 释放资源
当 `unique_ptr` **生命周期结束** 或 **调用** `**.reset()**` 时，自动释放资源，无需手动 `delete`。


```
p2.reset();  // 释放所管理对象
```

---
## 🧱 4. 配合自定义类型


```
struct Node {
    Node() { std::cout << "Node created\n"; }
    ~Node() { std::cout << "Node destroyed\n"; }
};
auto p = std::make_unique<Node>();  // 构造
// 自动析构，无需手动 delete
```

---
## 🎯 5. 配合数组 (C++17)


```
auto arr = std::make_unique<int[]>(5);
arr[0] = 10;
```

⚠️ **C++14 的 make_unique 不支持数组版本，C++17 之后才支持。**
---
## 🚨 常见注意事项
|   |   |
|---|---|
|⚠️ 注意事项|说明|
|**不能拷贝**|`unique_ptr` 只能 **move**，防止多次释放|
|**避免裸指针传递**|如果你用 `.get()` 拿到裸指针，记得不要 delete|
|**不要与 raw pointer 混用**|`unique_ptr` 管理的对象**只能有一个 owner**|
|**适合独占资源**|如文件句柄、socket、动态数组等|
|**避免循环引用**|不会产生循环引用问题，比 `shared_ptr` 更安全|
---
## 📝 快速笔记模板
```Plain
# unique_ptr + make_unique 使用笔记
✅ 功能：
- 智能指针，独占所有权，自动释放资源
- make_unique 是创建 unique_ptr 的推荐方式
📌 典型用法：
auto ptr = std::make_unique<T>(args...);
🎯 特点：
- 不能拷贝，只能 move
- 自动释放
- 适用于独占资源管理
⚠️ 注意事项：
- 不能拷贝
- 避免 get() 后 delete
- 避免与裸指针混用
```
---
## 🌟 推荐学习路径
如果你把 **shared_ptr, unique_ptr, make_shared, make_unique** 这四个一起学完，就掌握了 **现代 C++ 内存管理最核心工具**。
如果你想，我可以帮你做一张 **智能指针体系对比图**，让你一张图掌握它们的区别和使用场景。
要不要？
