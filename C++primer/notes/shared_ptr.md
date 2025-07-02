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
Created time: 2025-03-25T14:24
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
`std::shared_ptr` 是 C++11 引入的一种 **智能指针**，用于自动管理动态分配（`new` 出来）的对象的生命周期，避免手动调用 `delete` 导致内存泄漏或重复释放的问题。
---
## ✅ 一句话理解

> shared_ptr 是“共享式所有权”的智能指针，多个 shared_ptr 实例可以指向同一块内存，当最后一个引用它的 shared_ptr 被销毁时，对象才会被释放。
---
## 🌟 核心特性
|   |   |
|---|---|
|特性|说明|
|引用计数|内部维护一个计数器，记录有多少个 `shared_ptr` 指向该对象|
|自动释放|当引用计数归零（所有 `shared_ptr` 被销毁）时，自动调用 `delete`|
|可赋值与拷贝|可以像普通指针一样赋值、传递参数、拷贝|
|线程安全|引用计数操作是线程安全的（注意：不是所有操作都线程安全）|
---
## 📌 基本用法示例


```
\#include <iostream>
\#include <memory>
struct MyClass {
    MyClass() { std::cout << "构造\n"; }
    ~MyClass() { std::cout << "析构\n"; }
};
int main() {
    std::shared_ptr<MyClass> p1 = std::make_shared<MyClass>(); // 创建智能指针
    {
        std::shared_ptr<MyClass> p2 = p1; // 引用计数+1
        std::cout << "引用计数: " << p1.use_count() << "\n"; // 输出2
    } // p2 作用域结束，引用计数-1
    std::cout << "引用计数: " << p1.use_count() << "\n"; // 输出1
} // p1 结束，引用计数变0，自动析构对象
```

---
## 🧠 你需要记住的要点
- 使用 `std::make_shared<T>()` **更高效**、更安全。
- 不要用 `raw pointer`（裸指针）初始化多个 `shared_ptr`，否则会 **多次析构、程序崩溃**。
- `shared_ptr` 之间可以共享同一个对象，但不会自动打断循环引用（见下）。
---
## ⚠️ 常见陷阱：循环引用


```
struct A;
struct B;
struct A {
    std::shared_ptr<B> bptr;
};
struct B {
    std::shared_ptr<A> aptr;
};
// 如果 A 和 B 相互持有 shared_ptr，将导致引用计数永远不为0，内存泄漏！
```

✅ **解决办法**：其中一个改用 `std::weak_ptr`，不增加引用计数。
---
