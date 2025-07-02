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
Created time: 2025-04-10T15:06
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
太好了！你提到这个问题非常关键，是 C++ 构造函数 & 赋值操作中最容易混淆的点之一 —— **拷贝和移动为什么一个带** `**const**`**、一个不带？调用时怎么区分？**
我来一口气给你讲清楚这个背后的**本质原理**和**调用机制**👇
---
## ✅ 先看语法区别
|   |   |
|---|---|
|操作类型|典型签名|
|拷贝构造函数|`T(const T& other)`|
|移动构造函数|`T(T&& other)`|
|拷贝赋值运算符|`T& operator=(const T& other)`|
|移动赋值运算符|`T& operator=(T&& other)`|
---
## 🎯 为什么拷贝是 `const`，而移动不是？
### ✅ 拷贝函数：保证不修改原对象


```
T(const T& other)
```

拷贝的本质是**复制内容**，而不是改动它。  
所以传入的  
`other` 应该是 **常量引用**，避免你意外去改它。
---
### ✅ 移动函数：需要“抢”资源，必须能改！


```
T(T&& other)
```

移动的本质是**资源转移**：
你要从 `other` 那里“搬走资源”，那你就得**修改** `other`（例如把指针置为 `nullptr`）。
如果你写成：


```
T(const T&& other);  // ❌ 错误！不能修改 const 对象
```

你就不能清空原对象，移动就失去了意义。
---
## 🤔 那调用时，编译器是怎么区分调用哪个函数的？
靠的是 **传入对象是左值还是右值**。
### 🧩 举个例子：


```
MyClass a;
MyClass b = a;          // a 是左值 => 调用拷贝构造（const T&）
MyClass c = MyClass();  // MyClass() 是右值 => 调用移动构造（T&&）
MyClass d = std::move(a); // std::move(a) 是右值 => 调用移动构造
```

|   |   |   |
|---|---|---|
|表达式|值类别|调用函数|
|`a`|左值|`const T&`|
|`MyClass()`|右值|`T&&`|
|`std::move(a)`|右值|`T&&`|
---
## 🧠 小技巧：判断左值 / 右值
- 有名字的变量（比如 `a`） → 左值
- `std::move(x)`、临时对象 → 右值
---
## 📝 快速笔记总结
```Plain
# 拷贝 vs 移动：const 的区别
✅ 拷贝函数用 const：
- 不需要修改源对象
- 使用 const T&
✅ 移动函数不能 const：
- 需要修改源对象（清空资源）
- 使用 T&&（右值引用）
🎯 调用时依据：
- 左值 ⇒ 拷贝构造
- 右值 ⇒ 移动构造（如 std::move(x)）
📌 编译器会自动根据传入值的“值类别”选择最匹配的版本
```
---
## 🔥 你可以自己试试的小实验


```
struct MyType {
    MyType() {}
    MyType(const MyType&) { std::cout << "拷贝构造\n"; }
    MyType(MyType&&) { std::cout << "移动构造\n"; }
};
int main() {
    MyType a;
    MyType b = a;               // 拷贝构造
    MyType c = std::move(a);    // 移动构造
    MyType d = MyType();        // 移动构造
}
```

---
如果你想，我可以把这些做成一个**左值/右值判断小游戏小程序**，帮你加深记忆，要不要试试？👀
